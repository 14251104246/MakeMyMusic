package com.zehua.makemusic.resource;

import org.junit.Ignore;
import org.junit.Test;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class PreTest {
    final String PERFIX = "G:\\music\\";
    @Test
    public void testWavEdit() throws Exception {
        File fadedWav = new File(PERFIX+"Faded.wav");
        File outWav = new File(PERFIX+"testEdit.wav");

        FileInputStream inputStream = new FileInputStream(fadedWav);
        FileOutputStream outputStream = new FileOutputStream(outWav);

        byte[] head = new byte[1024];
        inputStream.read(head,0,head.length);
        outputStream.write(head);

        byte[] bufffer = new byte[1024];
        for (int i =1; i>0;){
            i= inputStream.read(bufffer);
            for(int cur =0;cur<bufffer.length;cur++){
                bufffer[cur]= (byte) (bufffer[cur]*Math.random()+bufffer[cur]);
            }
            outputStream.write(bufffer);
        }
    }

    public static class WaveHeader {
        public final char fileID[] = {'R', 'I', 'F', 'F'};
        public int fileLength;
        public char wavTag[] = {'W', 'A', 'V', 'E'};;
        public char FmtHdrID[] = {'f', 'm', 't', ' '};
        public int FmtHdrLeth;
        public short FormatTag;
        public short Channels;
        public int SamplesPerSec;
        public int AvgBytesPerSec;
        public short BlockAlign;
        public short BitsPerSample;
        public char DataHdrID[] = {'d','a','t','a'};
        public int DataHdrLeth;

        public byte[] getHeader() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            WriteChar(bos, fileID);
            WriteInt(bos, fileLength);
            WriteChar(bos, wavTag);
            WriteChar(bos, FmtHdrID);
            WriteInt(bos,FmtHdrLeth);
            WriteShort(bos,FormatTag);
            WriteShort(bos,Channels);
            WriteInt(bos,SamplesPerSec);
            WriteInt(bos,AvgBytesPerSec);
            WriteShort(bos,BlockAlign);
            WriteShort(bos,BitsPerSample);
            WriteChar(bos,DataHdrID);
            WriteInt(bos,DataHdrLeth);
            bos.flush();
            byte[] r = bos.toByteArray();
            bos.close();
            return r;
        }

        public static void WriteShort(ByteArrayOutputStream bos, int s) throws IOException {
            byte[] mybyte = new byte[2];
            mybyte[1] =(byte)( (s << 16) >> 24 );
            mybyte[0] =(byte)( (s << 24) >> 24 );
            bos.write(mybyte);
        }


        public static void WriteInt(ByteArrayOutputStream bos, int n) throws IOException {
            byte[] buf = new byte[4];
            buf[3] =(byte)( n >> 24 );
            buf[2] =(byte)( (n << 8) >> 24 );
            buf[1] =(byte)( (n << 16) >> 24 );
            buf[0] =(byte)( (n << 24) >> 24 );
            bos.write(buf);
        }

        public static void WriteChar(ByteArrayOutputStream bos, char[] id) {
            for (int i=0; i<id.length; i++) {
                char c = id[i];
                bos.write(c);
            }
        }
    }
    @Ignore
    public void testCombinePlayMp3() throws Exception {
        File fadedMp3 = new File(PERFIX+"Faded.mp3");
        File girlMp3 = new File(PERFIX+"森永真由美 - Mermaid girl (Extended RRver.).mp3");

        AudioInputStream fadedMp3InputStream = AudioSystem.getAudioInputStream(fadedMp3);
        AudioInputStream girlMp3InputStream = AudioSystem.getAudioInputStream(girlMp3);

        AudioFormat format = fadedMp3InputStream.getFormat();
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(), 16, format.getChannels(),
                format.getChannels() * 2, format.getSampleRate(), false);
        fadedMp3InputStream = AudioSystem.getAudioInputStream(format, fadedMp3InputStream);
        girlMp3InputStream = AudioSystem.getAudioInputStream(format,girlMp3InputStream);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                format);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
                .getLine(info);
        sourceDataLine.open(fadedMp3InputStream.getFormat(), sourceDataLine.getBufferSize());
        sourceDataLine.start();

        byte[] fadedBuf = new byte[sourceDataLine.getBufferSize()];
        byte[] girlBuf = new byte[sourceDataLine.getBufferSize()];


        for(int numRead =1;numRead>0;){
            numRead= fadedMp3InputStream.read(fadedBuf, 0, fadedBuf.length);
            girlMp3InputStream.read(girlBuf, 0, girlBuf.length);

            for(int i=numRead;i>0;i--){
                int i1 = (int) (fadedBuf[i] - girlBuf[i]);
                fadedBuf[i]= (byte)i1;
            }
            int offset = 0;
            while (offset < numRead) {
                offset += sourceDataLine.write(fadedBuf, offset, numRead - offset);
            }
        }


    }
    @Ignore
    public void testPlayMp3_Jlayer() throws Exception {
        File fadedMp3 = new File(PERFIX+"Faded.mp3");


        AudioInputStream fadeStream = AudioSystem.getAudioInputStream(fadedMp3);
        AudioFormat format = fadeStream.getFormat();
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(), 16, format.getChannels(),
                    format.getChannels() * 2, format.getSampleRate(), false);
            fadeStream = AudioSystem.getAudioInputStream(format, fadeStream);
        }

        //播放
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                fadeStream.getFormat());
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
                .getLine(info);
        sourceDataLine.open(fadeStream.getFormat(), sourceDataLine.getBufferSize());
        sourceDataLine.start();
        int numRead = 0;
        byte[] buf = new byte[sourceDataLine.getBufferSize()];
        while ((numRead = fadeStream.read(buf, 0, buf.length)) >= 0) {
            int offset = 0;
            while (offset < numRead) {
                offset += sourceDataLine.write(buf, offset, numRead - offset);
            }
            System.out.println(sourceDataLine.getFramePosition() + " " + sourceDataLine.getMicrosecondPosition());
        }
        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
        fadeStream.close();
    }

}