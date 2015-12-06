package com.example.smartcart.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.smartcart.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by usrivast on 06/12/15.
 */
public class NotificationFragment extends android.support.v4.app.ListFragment {

    public  static ArrayAdapter<String> mAdapter = null;
    public  static ArrayList<String> mValues = null;
    public static HashMap<String, Boolean> mKeys = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_fragment, container,
                false);

        mValues = new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mValues);
        setListAdapter(mAdapter);
        mKeys = new HashMap<String, Boolean>();
        return rootView;
    }
}
