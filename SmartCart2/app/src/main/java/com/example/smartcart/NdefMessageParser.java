/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.smartcart;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartcart.record.ParsedNdefRecord;
import com.example.smartcart.record.SmartPoster;
import com.example.smartcart.record.TextRecord;
import com.example.smartcart.record.UriRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

    static loginPage mActivity = null;
    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static List<ParsedNdefRecord> parse(NdefMessage message, loginPage activity) {
        mActivity = activity;
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {

                TextView text = (TextView)mActivity.findViewById(R.id.rfidtext);
                text.setText(new String(record.getPayload()));
            }
        }
        return elements;
    }
}
