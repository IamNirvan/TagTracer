package com.tagtracer.util;

import android.util.Log;

import com.tagtracer.models.RFIDTag;
import com.tagtracer.util.callbacks.TagConsumerCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Queue;

public class TagConsumer {
    private final String TAG = TagConsumer.class.getSimpleName();
    private Thread consumerThread;
    private volatile boolean consumeTags = false;
    private final Object lock = new Object();

    /**
     * This returns a runnable that polls the concurrent queue and passes each item in the queue
     * into the callback function for processing. It uses a loop with a flag to start/pause the
     * loop for the poll.
     * @param queue concurrent queue from the rfid reader impl
     * @param handler callback function
     * @return consumer as a runnable instance
     * */
    public Runnable getConsumer(@NotNull Queue<RFIDTag> queue, @NotNull TagConsumerCallback<RFIDTag> handler) {
        return () -> {
            while (true) {
                synchronized (this.lock) {
                    while (!this.consumeTags) {
                        try {
                            this.lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Reset the interrupted flag (gets set to true)
                            Log.d(TAG, "getConsumer: consumer thread interrupted");
                            this.cleanUp();
                            return;
                        }
                    }
                }

                // Start consuming the queue
                RFIDTag tag = queue.poll();
                if (tag != null) {
                    handler.consume(tag);
                }
            }
        };
    }

    /**
     * Use this when you want to start the tag consumer.
     * If the thread has not been initialized, <b>a new thread will be created and started.
     * If already initialized, then the existing thread will be used<b/>
     * @param runnable runnable that represents the tag consumer logic that runs on the consumer thread
     * @return true if consuming tags. False if not consuming tags.
     * */
    public boolean startConsumer(@NotNull Runnable runnable) {
        if (this.consumerThread == null || !this.consumerThread.isAlive()) {
            this.consumerThread = new Thread(runnable);
            this.consumerThread.start();
            Log.d(TAG, "startConsumer: created new consumer thread");
        }

        // Start consuming tags
        synchronized (this.lock) {
            this.consumeTags = true;
            Log.d(TAG, "startConsumer: started consumer");
            this.lock.notify();
        }

        return this.consumeTags;
    }

    /**
     * Use this when you want to stop polling the concurrent queue.
     * <b>This does not destroy the consumer thread.</b>
     * @return False if stopped consuming tags (polling). Otherwise true.
     * */
    public boolean stopConsumer() {
        synchronized (lock) {
            if (this.consumeTags) {
                this.consumeTags = false;
                Log.d(TAG, "stopConsumer: stopped consumer");
            }
        }
        return this.consumeTags;
    }

    /**
     * Use this when you want to stop and destroy the consumer thread
     * @throws SecurityException throws this if the thread fails to stop
     * */
    public void dispose() throws SecurityException {
        synchronized (this.lock) {
            this.stopConsumer();

            if (this.consumerThread != null) {
                try {
                    this.consumerThread.interrupt();
                    Log.d(TAG, "dispose: disposing tag consumer");
                    this.consumerThread.join(500);
                } catch (SecurityException ex) {
                    Log.e(TAG, "dispose: failed to stop tag consumer", ex);
                    throw ex;
                } catch (InterruptedException ex) {
                    // This gets thrown when the thread that calls this function gets interrupted
                    // while waiting for the consumer thread to join...
                    Thread.currentThread().interrupt(); // Reset the interrupted flag (gets set to true)
                    throw new RuntimeException("Thread interrupted while disposing consumer", ex);
                } finally {
                    this.cleanUp();
                }
            } else {
                Log.w(TAG, "dispose: consumer thread is null... cannot stop thread");
            }
        }
    }

    /**
     * If there are any resources that need to be disposed of, use this function.
     * */
    private void cleanUp() {
        this.consumeTags = false;
        Log.d(TAG, "cleanUp: disposed resources");
    }
}
