package com.googlecode.mp4parser.boxes.mp4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part1.objectdescriptors.AudioSpecificConfig;
import org.mp4parser.boxes.iso14496.part1.objectdescriptors.DecoderConfigDescriptor;
import org.mp4parser.boxes.iso14496.part14.ESDescriptorBox;
import org.mp4parser.tools.ByteBufferByteChannel;
import org.mp4parser.tools.Hex;
import org.mp4parser.tools.Path;

public class ESDescriptorBoxTest {
    @Test
    public void testEsDescriptor() throws Exception {
        String esdsBytes = "0000002A6573647300000000031C000000041440150018000001F4000001F4000505131056E598060102";
        //String esdsBytes = "0000003365736473000000000380808022000200048080801440150000000006AD650006AD65058080800211B0068080800102";
        IsoFile isoFile = new IsoFile(new ByteBufferByteChannel(Hex.decodeHex(esdsBytes)));
        ESDescriptorBox esds = Path.getPath(isoFile, "esds");
        assertNotNull(esds);
        assertNotNull(esds.getEsDescriptor());
        DecoderConfigDescriptor dcd = esds.getEsDescriptor().getDecoderConfigDescriptor();  
        assertNotNull(dcd);
        assertEquals(64, dcd.getObjectTypeIndication());
        assertEquals(5, dcd.getStreamType());
        assertEquals(0, dcd.getUpStream());
        assertEquals(6144, dcd.getBufferSizeDB());
        assertEquals(128000, dcd.getMaxBitRate());
        assertEquals(128000, dcd.getAvgBitRate());
        AudioSpecificConfig asc = dcd.getAudioSpecificInfo();
        assertNotNull(asc);
        assertEquals("131056E598", Hex.encodeHex(asc.getConfigBytes()));
        assertEquals(2, asc.getAudioObjectType());
        assertEquals(24000, asc.getSamplingFrequency());
        assertEquals(48000, asc.getExtensionSamplingFrequency());
        assertEquals(2, asc.getChannelConfiguration());
    }
}