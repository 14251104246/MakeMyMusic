
import org.junit.Ignore;
import org.junit.Test;

import javax.sound.sampled.*;
import java.io.*;

public class PreTest {
    final String PERFIX = "G:\\music\\";
    @Test
    public void testWavEdit() throws Exception {
        File fadedWav = new File(PERFIX+"Faded.wav");
        File outWav = new File(PERFIX+"testEdit.wav");

        FileInputStream inputStream = new FileInputStream(fadedWav);
        FileOutputStream outputStream = new FileOutputStream(outWav);

        byte[] head = new byte[656];
        inputStream.read(head,0,head.length);
        outputStream.write(head);

        byte[] buffer = new byte[1024];
        for (int i =1; i>0;){
            i= inputStream.read(buffer);
            for(int cur =0;cur<buffer.length;cur++){
                buffer[cur]= (byte) (buffer[cur]*Math.random()+buffer[cur]);
            }
            outputStream.write(buffer);
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