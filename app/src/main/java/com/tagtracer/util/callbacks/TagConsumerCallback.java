package com.tagtracer.util.callbacks;

public interface TagConsumerCallback<RFIDTag> {
    void consume(RFIDTag rfidTag);
}
