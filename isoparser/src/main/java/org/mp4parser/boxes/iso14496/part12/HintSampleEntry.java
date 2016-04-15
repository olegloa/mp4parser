package org.mp4parser.boxes.iso14496.part12;


import static org.mp4parser.tools.ChannelHelper.readFully;
import static org.mp4parser.tools.ChannelHelper.writeFully;

import org.mp4parser.BoxParser;
import org.mp4parser.boxes.sampleentry.AbstractSampleEntry;
import org.mp4parser.tools.CastUtils;
import org.mp4parser.tools.IsoTypeReader;
import org.mp4parser.tools.IsoTypeWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class HintSampleEntry extends AbstractSampleEntry {
    protected byte[] data;

    public HintSampleEntry(String type) {
        super(type);
    }

    @Override
    public void parse(ReadableByteChannel dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        ByteBuffer b1 = readFully(dataSource, 8);
        b1.position(6);
        dataReferenceIndex = IsoTypeReader.readUInt16(b1);
        data = new byte[CastUtils.l2i(contentSize - 8)];
        readFully(dataSource, ByteBuffer.wrap(data));
    }

    @Override
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writeFully(writableByteChannel, getHeader());
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.position(6);
        IsoTypeWriter.writeUInt16(byteBuffer, dataReferenceIndex);
        byteBuffer.rewind();
        writeFully(writableByteChannel, byteBuffer);
        writeFully(writableByteChannel, ByteBuffer.wrap(data));
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    @Override
    public long getSize() {
        long s = 8 + data.length;
        return s + ((largeBox || (s + 8) >= (1L << 32)) ? 16 : 8);
    }
}
