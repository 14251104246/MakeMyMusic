package com.zehua.makemusic;

import com.zehua.makemusic.strategy.DefaultEditor;
import com.zehua.makemusic.strategy.IEditor;
import com.zehua.makemusic.util.HexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WavEdit {
    final String PERFIX = "G:\\music\\";
    File inWav = new File(PERFIX+"Faded.wav");
    File outWav = new File(PERFIX+"testEdit.wav");
    private IEditor editor = new DefaultEditor();
    public void execute() throws Exception{
        FileInputStream inputStream = new FileInputStream(inWav);
        FileOutputStream outputStream = new FileOutputStream(outWav);

        byte[] head = new byte[HexUtils.getWavDataPosition(inWav)];
        inputStream.read(head,0,head.length);
        outputStream.write(head);

        byte[] buffer = new byte[1024];
        for (int i =1; i>0;){
            i= inputStream.read(buffer);
            editBuffer(buffer);
            outputStream.write(buffer);
        }
    }

    private void editBuffer(byte[] buffer) {
        editor.edit(buffer);
    }

    public void setEditor(IEditor editor) {
        this.editor = editor;
    }

    public void setInWav(File inWav) {
        this.inWav = inWav;
    }

    public void setOutWav(File outWav) {
        this.outWav = outWav;
    }
}
