package com.zehua.makemusic.strategy;

public class DefaultEditor implements IEditor {
    public byte[] edit(byte[] buffer) {
        for(int cur =0;cur<buffer.length;cur++){
            buffer[cur]= (byte) (buffer[cur]*Math.random()+buffer[cur]);
        }
        return buffer;
    }
}
