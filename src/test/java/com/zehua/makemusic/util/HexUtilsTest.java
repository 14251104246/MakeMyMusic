package com.zehua.makemusic.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class HexUtilsTest {
    final String PERFIX = "G:\\music\\";
    @Test
    public void getWavDataPosition() throws Exception {
        File fadedWav = new File(PERFIX+"girl.wav");
        assertTrue(HexUtils.getWavDataPosition(fadedWav)>0);
    }

    @Test
    public void compareByte() throws Exception {
    }

}