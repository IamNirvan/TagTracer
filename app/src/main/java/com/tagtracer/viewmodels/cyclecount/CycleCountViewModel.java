package com.tagtracer.viewmodels.cyclecount;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tagtracer.models.RFIDTag;

import java.util.ArrayList;

public class CycleCountViewModel extends ViewModel {
    private static final String TAG = CycleCountViewModel.class.getName();
    public MutableLiveData<ArrayList<RFIDTag>> tagSet = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Integer> counter = new MutableLiveData<>(0);

    public void addScannedTag(RFIDTag tag) {
        ArrayList<RFIDTag> map = tagSet.getValue();
        if (map != null) {
            map.add(tag);
            tagSet.postValue(map);
            counter.postValue(tagSet.getValue().size());
        } else {
            Log.e(TAG, "addScannedTag: cannot update the list since it is null");
        }
    }

    public ArrayList<RFIDTag> getTags() {
        ArrayList<RFIDTag> map = tagSet.getValue();
        if (map == null) {
            return new ArrayList<>();
        }
        return map;
    }
}
