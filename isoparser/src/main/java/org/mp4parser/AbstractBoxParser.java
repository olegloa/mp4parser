/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mp4parser;

import static org.mp4parser.tools.ChannelHelper.readFully;

import org.mp4parser.boxes.UserBox;
import org.mp4parser.tools.ChannelHelper;
import org.mp4parser.tools.Hex;
import org.mp4parser.tools.IsoTypeReader;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This BoxParser handles the basic stuff like reading size and extracting box type.
 */
public abstract class AbstractBoxParser implements BoxParser {

    private static Logger LOG = Logger.getLogger(AbstractBoxParser.class.getName());

    private ThreadLocal<ByteBuffer> header = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(32);
        }
    };

    public abstract ParsableBox createBox(String type, byte[] userType, String parent);

    /**
     * Parses the next size and type, creates a box instance and parses the box's content.
     *
     * @param byteChannel the DataSource pointing to the ISO file
     * @param parentType  the current box's parent's type (null if no parent)
     * @return the box just parsed
     * @throws java.io.IOException if reading from <code>in</code> fails
     */
    public ParsableBox parseBox(ReadableByteChannel byteChannel, String parentType) throws IOException {
        ByteBuffer header = this.header.get();

        header.rewind().limit(8);
        readFully(byteChannel, header);
        header.rewind();

        long size = IsoTypeReader.readUInt32(header);
        // do plausibility check
        if (size < 8 && size > 1) {
            LOG.severe("Plausibility check failed: size < 8 (size = " + size + "). Stop parsing!");
            throw new EOFException();
        }

        String type = IsoTypeReader.read4cc(header);
        //System.err.println(type);
        byte[] usertype = null;
        long contentSize;

        if (size == 1) {
            header.limit(16);
            readFully(byteChannel, header);
            header.position(8);
            size = IsoTypeReader.readUInt64(header);
            contentSize = size - 16;
        } else if (size == 0) {
            throw new RuntimeException("box size of zero means 'till end of file. That is not yet supported");
        } else {
            contentSize = size - 8;
        }
        if (UserBox.TYPE.equals(type)) {
            header.limit(header.limit() + 16);
            readFully(byteChannel, header);
            usertype = new byte[16];
            for (int i = header.position() - 16; i < header.position(); i++) {
                usertype[i - (header.position() - 16)] = header.get(i);
            }
            contentSize -= 16;
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Creating " + type + " " + Hex.encodeHex(new byte[]{header.get(4), header.get(5), header.get(6), header.get(7)}));
        }
        ParsableBox parsableBox = createBox(type, usertype, parentType);
        //LOG.finest("Parsing " + box.getType());
        // System.out.println("parsing " + Mp4Arrays.toString(box.getType()) + " " + box.getClass().getName() + " size=" + size);
        header.rewind();

        parsableBox.parse(byteChannel, header, contentSize, this);
        return parsableBox;
    }


}
