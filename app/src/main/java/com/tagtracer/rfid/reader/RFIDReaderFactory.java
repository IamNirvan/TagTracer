package com.tagtracer.rfid.reader;

import android.content.Context;
import android.util.Log;

import com.tagtracer.enums.ERFIDReaderType;
import com.tagtracer.rfid.reader.chainway.ChainwayReaderV1;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class RFIDReaderFactory {
    public static final String TAG = RFIDReaderFactory.class.getName();
    private static RFIDReaderFactory instance;

    private RFIDReaderFactory(){}

    public static RFIDReaderFactory getInstance() {
        if (instance == null) {
            instance = new RFIDReaderFactory();
        }
        return instance;
    }

    public IRFIDReader getRfidReader(@NotNull String type, @ApplicationContext Context context) throws Exception {
        if (type.equalsIgnoreCase(ERFIDReaderType.CHAINWAY.toString())) {
            return new ChainwayReaderV1(context);
        } else {
            Log.e(TAG, "getInstance: cannot determine reader type");
            throw new Exception("unrecognized reader type");
        }
    }

}
