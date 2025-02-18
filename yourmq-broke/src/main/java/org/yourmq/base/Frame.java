package org.yourmq.base;

public class Frame {
    private int flag;
    private MessageInternal message;

    public Frame(int flag, MessageInternal message) {
        this.flag = flag;
        this.message = message;
    }

    public int flag() {
        return this.flag;
    }

    public MessageInternal message() {
        return this.message;
    }

    @Override
    public String toString() {
        return "Frame{flag=" + Flags.name(this.flag) + ", message=" + this.message + '}';
    }
}
