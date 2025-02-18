

package org.yourmq.base;

import org.yourmq.common.Entity;
import org.yourmq.common.EntityDefault;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public abstract class FragmentHandlerBase implements FragmentHandler {
    public FragmentHandlerBase() {
    }
@Override
    public void spliFragment(Channel channel, StreamInternal stream, MessageInternal message, IoConsumer<Entity> consumer) throws IOException {
        if (message.dataSize() <= channel.getConfig().getFragmentSize() && !(message.data() instanceof MappedByteBuffer)) {
            consumer.accept(message);
            if (stream != null) {
                stream.onProgress(true, 1, 1);
            }

        } else {
            int fragmentTotal = message.dataSize() / channel.getConfig().getFragmentSize();
            if (message.dataSize() % channel.getConfig().getFragmentSize() > 0) {
                ++fragmentTotal;
            }

            int fragmentIndex = 0;

            while(true) {
                ++fragmentIndex;
                ByteBuffer dataBuffer = this.readFragmentData(message.data(), channel.getConfig().getFragmentSize());
                if (dataBuffer == null || dataBuffer.limit() == 0) {
                    return;
                }

                EntityDefault fragmentEntity = (new EntityDefault()).dataSet(dataBuffer);
                if (fragmentIndex == 1) {
                    fragmentEntity.metaMapPut(message.metaMap());
                }

                if (fragmentTotal > 1) {
                    fragmentEntity.metaPut("Data-Fragment-Idx", String.valueOf(fragmentIndex));
                    fragmentEntity.metaPut("Data-Fragment-Total", String.valueOf(fragmentTotal));
                }

                consumer.accept(fragmentEntity);
                if (stream != null) {
                    stream.onProgress(true, fragmentIndex, fragmentTotal);
                }
            }
        }
    }
@Override
    public Frame aggrFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException {
        FragmentAggregator aggregator = (FragmentAggregator)channel.getAttachment(message.sid());
        if (aggregator == null) {
            aggregator = this.createFragmentAggregator(message);
            channel.putAttachment(aggregator.getSid(), aggregator);
        }

        aggregator.add(fragmentIndex, message);
        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            return null;
        } else {
            channel.putAttachment(message.sid(), (Object)null);
            return aggregator.get();
        }
    }

    protected abstract FragmentAggregator createFragmentAggregator(MessageInternal message) throws IOException;

    protected ByteBuffer readFragmentData(ByteBuffer ins, int maxSize) {

        int size;
        if (ins.remaining() > maxSize) {
            size = maxSize;
        } else {
            size = ins.remaining();
        }

        if (size == 0) {
            return null;
        } else {
            byte[] bytes = new byte[size];
            ins.get(bytes);
            return ByteBuffer.wrap(bytes);
        }
    }
}
