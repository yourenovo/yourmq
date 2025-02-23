
package org.yourmq.base;

public class FragmentHolder {
    private int index;
    private MessageInternal message;

    public FragmentHolder(int index, MessageInternal message) {
        this.index = index;
        this.message = message;
    }

    public int getIndex() {
        return this.index;
    }

    public MessageInternal getMessage() {
        return this.message;
    }
}
