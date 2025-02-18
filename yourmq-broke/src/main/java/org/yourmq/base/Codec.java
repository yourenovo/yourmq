package org.yourmq.base;

import java.io.IOException;
import java.util.function.Function;

public interface Codec {
    Frame read(CodecReader reader);

    <T extends CodecWriter> T write(Frame frame, Function<Integer, T> writerFactory) throws IOException;
}
