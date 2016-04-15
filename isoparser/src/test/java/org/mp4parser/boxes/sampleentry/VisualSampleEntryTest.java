package org.mp4parser.boxes.sampleentry;

import com.googlecode.mp4parser.boxes.BoxWriteReadBase;

import java.util.Collections;
import java.util.Map;

/**
 * Created by sannies on 23.05.13.
 */
public class VisualSampleEntryTest extends BoxWriteReadBase<VisualSampleEntry> {
    @Override
    public Class<VisualSampleEntry> getBoxUnderTest() {
        return VisualSampleEntry.class;
    }

    @Override
    public void setupProperties(Map<String, Object> addPropsHere, VisualSampleEntry box) {
        addPropsHere.put("boxes", Collections.EMPTY_LIST);
        addPropsHere.put("compressorname", "avc1");
        addPropsHere.put("dataReferenceIndex", 1);
        addPropsHere.put("depth", 1);
        addPropsHere.put("frameCount", 25);
        addPropsHere.put("height", 200);
        addPropsHere.put("width", 320);
        addPropsHere.put("horizresolution", 320.0);
        addPropsHere.put("vertresolution", 200.0);
    }
}
