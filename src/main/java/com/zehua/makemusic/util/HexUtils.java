package com.zehua.makemusic.util;

import java.io.*;

public class HexUtils {
//    public final char[] fileID = {'R', 'I', 'F', 'F'};
//    public int fileLength;
//    public char[] wavTag = {'W', 'A', 'V', 'E'};
//    public char[] FmtHdrID = {'f', 'm', 't', ' '};
//    public int FmtHdrLeth;
//    public short FormatTag;
//    public short Channels;
//    public int SamplesPerSec;
//    public int AvgBytesPerSec;
//    public short BlockAlign;
//    public short BitsPerSample;
//    public char[] DataHdrID = {'d', 'a', 't', 'a'};
//    public int DataHdrLeth;
//
//    public byte[] getHeader() throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        WriteChar(bos, fileID);
//        WriteInt(bos, fileLength);
//        WriteChar(bos, wavTag);
//        WriteChar(bos, FmtHdrID);
//        WriteInt(bos, FmtHdrLeth);
//        WriteShort(bos, FormatTag);
//        WriteShort(bos, Channels);
//        WriteInt(bos, SamplesPerSec);
//        WriteInt(bos, AvgBytesPerSec);
//        WriteShort(bos, BlockAlign);
//        WriteShort(bos, BitsPerSample);
//        WriteChar(bos, DataHdrID);
//        WriteInt(bos, DataHdrLeth);
//        bos.flush();
//        byte[] r = bos.toByteArray();
//        bos.close();
//        return r;
//    }

    public static void WriteShort(ByteArrayOutputStream bos, int s) throws IOException {
        byte[] mybyte = new byte[2];
        mybyte[1] = (byte) ((s << 16) >> 24);
        mybyte[0] = (byte) ((s << 24) >> 24);
        bos.write(mybyte);
    }

    public static void WriteInt(ByteArrayOutputStream bos, int n) throws IOException {
        byte[] buf = new byte[4];
        buf[3] = (byte) (n >> 24);
        buf[2] = (byte) ((n << 8) >> 24);
        buf[1] = (byte) ((n << 16) >> 24);
        buf[0] = (byte) ((n << 24) >> 24);
        bos.write(buf);
    }

    public static void WriteChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; i++) {
            char c = id[i];
            bos.write(c);
        }
    }

    /**
     * 获得wav波形数据的开始位置
     * @param inputWavFile
     * @return
     * @throws Exception
     */
    public static int getWavDataPosition(File inputWavFile) throws Exception {
        FileInputStream inputStream = new FileInputStream(inputWavFile);
        byte[] head = new byte[1024];
        inputStream.read(head,0,head.length);
        return compareByte(head);
    }

    protected static int compareByte(byte[] head) {
        final byte[] dataMark = {'d', 'a', 't', 'a'} ;
        for(int i = 0; i<head.length;i++){
            if(head[i]!=dataMark[0]){
                continue;
            }
            i++;
            for(int dataIndex = 1;dataIndex<dataMark.length;){
                if(head[i++]!=dataMark[dataIndex++]) break;

                if(dataMark.length-1<dataIndex){
                    return i+4;
                }

            }
        }
        return -1;
    }
}