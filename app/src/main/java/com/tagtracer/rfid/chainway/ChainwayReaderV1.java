package com.tagtracer.rfid.chainway;

import android.content.Context;
import android.util.Log;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.Gen2Entity;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.tagtracer.enums.ERFIDReaderSession;
import com.tagtracer.rfid.IRFIDReader;
import com.tagtracer.models.RFIDTag;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class ChainwayReaderV1 extends IRFIDReader {
    public static final String TAG = ChainwayReaderV1.class.getName();
    private RFIDWithUHFUART reader;
    public boolean readTag = false;
    private Queue<RFIDTag> queue = new ConcurrentLinkedQueue<>();

    public ChainwayReaderV1(@ApplicationContext Context context) {
        this.initializeReader(context);
    }

    /**
     * Obtains and initializes the RFIDWithUHFUART instance
     * @param context application context
     * */
    private void initializeReader(@ApplicationContext Context context) {
        try {
            this.reader = RFIDWithUHFUART.getInstance();
            this.reader.init();
            Log.d(TAG, "initializeReader: initialized chainway reader instance");
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Queue<RFIDTag> getQueue() {
        return this.queue;
    }

    @Override
    public boolean setPowerLevel(int power) throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "setPowerLevel: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }
        Log.d(TAG, String.format("setting power level %s", power));
        return this.reader.setPower(power);
    }

    @Override
    public int getPowerLevel() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "getPowerLevel: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }
        return this.reader.getPower();
    }

    @Override
    public boolean setGen2(@NotNull Gen2Entity entity) throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "setGen2: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }
        return this.reader.setGen2(entity);
    }

    @Override
    public Gen2Entity getGen2() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "getGen2: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }
        return this.reader.getGen2();
    }

    @Override
    public boolean setSession(int sessionId, int target) throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "setSession: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }

        // Validate the sessionId and target
        if (sessionId < 0 || target < 0) {
            Log.e(TAG, "setSession: invalid session id or target");
            throw new RuntimeException("invalid session id or target");
        }

        Gen2Entity gen2Entity = this.getGen2();
        if (gen2Entity == null) {
            Log.e(TAG, "setSession: gen2 entity is null");
            throw new RuntimeException("gen2 entity is null");
        }

        // Handle the gen2 entity
        Log.d(TAG, String.format("setting sessionId = %s target = %s", sessionId, target));
        gen2Entity.setQuerySession(sessionId);
        gen2Entity.setQueryTarget(target);
        return this.setGen2(gen2Entity);
    }

    @Override
    public Map<String, Integer> getSession() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "getSession: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }

        Gen2Entity gen2Entity = this.getGen2();
        if (gen2Entity == null) {
            Log.e(TAG, "getSession: gen2 entity is null");
            throw new RuntimeException("gen2 entity is null");
        }

        // Fetch the session details
        Map<String, Integer> sessionMap = new HashMap<>();
        sessionMap.put(ERFIDReaderSession.SESSION.getName(), gen2Entity.getQuerySession());
        sessionMap.put(ERFIDReaderSession.TARGET.getName(), gen2Entity.getQueryTarget());
        return sessionMap;
    }

    @Override
    public boolean startInventory() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "startInventory: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }

        if (this.reader.startInventoryTag()) {
            new TagProcessor().start();
            this.readTag = true;
            Log.d(TAG, "startInventory: started reading tags");
            return true;
        } else {
            Log.w(TAG, "startInventory: reading has already started");
        }
        return false;
    }

    @Override
    public boolean stopInventory() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "stopInventory: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }

        if (this.readTag) {
            this.readTag = false;
            Log.d(TAG, "startInventory: stopped reading tags");
            return this.reader.stopInventory();
        } else {
            Log.w(TAG, "stopInventory: reading has already stopped");
        }
        return false;
    }

    @Override
    public void dispose() throws RuntimeException {
        if (this.reader == null) {
            Log.e(TAG, "dispose: reader instance is null");
            throw new RuntimeException("reader instance is null");
        }
        this.reader.free();
    }

    private class TagProcessor extends Thread {
        @Override
        public void run() {
            UHFTAGInfo tag;

            while (readTag) {
                tag = reader.readTagFromBuffer();
                if (tag != null) {
                    queue.add(new RFIDTag(tag.getEPC(), tag.getRssi()));
                    Log.d(TAG, "epc = " + tag.getEPC());
                }
            }
        }
    }
}
