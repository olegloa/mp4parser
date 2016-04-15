package org.mp4parser.tools;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelHelper {
    private static ByteBuffer EMPTY = ByteBuffer.wrap(new byte[0]);

    public static void readFully(ReadableByteChannel channel, ByteBuffer dst) throws IOException {
        readFully(channel, dst, dst.remaining());
    }

    public static ByteBuffer readFully(ReadableByteChannel channel, int count) throws IOException {
        if (count == 0) {
            return EMPTY;
        }

        if (channel instanceof FileChannel) {
            FileChannel fChannel = (FileChannel) channel;

            ByteBuffer bb = fChannel.map(FileChannel.MapMode.READ_ONLY, fChannel.position(), count);
            fChannel.position(fChannel.position() + count);

            return bb;
        }

        if (channel instanceof ByteBufferByteChannel) {
            ByteBufferByteChannel bbChannel = (ByteBufferByteChannel) channel;

            if (bbChannel.byteBuffer.remaining() < count) {
                throw new IOException();
            }

            ByteBuffer src = bbChannel.byteBuffer;
            ByteBuffer dst = (ByteBuffer) src.slice().limit(count);
            src.position(src.position() + count);

            return dst;
        }

        ByteBuffer buf = ByteBuffer.allocate(count);
        readFully(channel, buf);
        buf.rewind();

        return buf;
    }

    public static void readFully(ReadableByteChannel channel, ByteBuffer dst, int count) throws IOException {
        while (dst.hasRemaining()) {
            int read = channel.read(dst);
            if (read < 0) {
                throw new EOFException();
            }
        }
    }

    public static void writeFully(WritableByteChannel channel, ByteBuffer src) throws IOException {
        do {
            int written = channel.write(src);
            if (written < 0) {
                throw new EOFException();
            }
        } while (src.hasRemaining());
    }
}