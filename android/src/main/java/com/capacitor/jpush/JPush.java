package com.capacitor.jpush;

import com.getcapacitor.JSArray;
import com.getcapacitor.PluginCall;

public class JPush {
    public JSArray getTagsValue(PluginCall call) {
        JSArray tags = call.getArray("tags", JSArray.from(String.class));
        if (tags == null || tags.length() == 0) {
            call.reject("tags参数不能为空");
            return new JSArray();
        }
        return tags;
    }

    public Integer sequenceNumber(PluginCall call) {
        int defaultValue = -1;
        Integer sequence = call.getInt("sequence", defaultValue);
        if(sequence != null) {
            return sequence;
        }
        return defaultValue;
    }
}
