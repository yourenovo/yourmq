package org.yourmq.base;

import java.io.IOException;

public interface FragmentAggregator {
    String getSid();

    int getDataStreamSize();

    int getDataLength();

    void add(int index, MessageInternal message) throws IOException;

    Frame get() throws IOException;
}