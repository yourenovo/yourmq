//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.yourmq.common.EntityDefault;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FragmentAggregatorDefault implements FragmentAggregator {
    private MessageInternal main;
    private List<FragmentHolder> fragmentHolders = new ArrayList();
    private int dataStreamSize;
    private int dataLength;

    public FragmentAggregatorDefault(MessageInternal main) {
        this.main = main;
        String dataLengthStr = main.meta("Data-Length");
    }
@Override
    public String getSid() {
        return this.main.sid();
    }
    @Override

    public int getDataStreamSize() {
        return this.dataStreamSize;
    }
    @Override

    public int getDataLength() {
        return this.dataLength;
    }
    @Override

    public void add(int index, MessageInternal message) throws IOException {
        this.fragmentHolders.add(new FragmentHolder(index, message));
        this.dataStreamSize += message.dataSize();
    }
    @Override

    public Frame get() throws IOException {
        this.fragmentHolders.sort(Comparator.comparing((fhx) -> {
            return fhx.getIndex();
        }));
        ByteBuffer dataBuffer = ByteBuffer.allocate(this.dataLength);
        Iterator var2 = this.fragmentHolders.iterator();

        while(var2.hasNext()) {
            FragmentHolder fh = (FragmentHolder)var2.next();
            dataBuffer.put(fh.getMessage().data().array());
        }

        dataBuffer.flip();
        EntityDefault entity = (new EntityDefault()).metaMapPut(this.main.metaMap()).dataSet(dataBuffer);
        entity.metaMap().remove("Data-Fragment-Idx");
        return new Frame(this.main.flag(), (new MessageBuilder()).flag(this.main.flag()).sid(this.main.sid()).event(this.main.event()).entity(entity).build());
    }
}
