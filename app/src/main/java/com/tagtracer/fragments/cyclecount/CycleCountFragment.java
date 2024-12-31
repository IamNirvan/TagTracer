package com.tagtracer.fragments.cyclecount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tagtracer.databinding.FragmentCyclecountBinding;
import com.tagtracer.util.IDisposable;

public class CycleCountFragment extends Fragment implements IDisposable {
    public static final String TAG = CycleCountFragment.class.getName();
    private FragmentCyclecountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentCyclecountBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void dispose() {

    }
}
