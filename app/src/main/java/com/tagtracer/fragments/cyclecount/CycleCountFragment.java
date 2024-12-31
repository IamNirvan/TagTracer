package com.tagtracer.fragments.cyclecount;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tagtracer.MainActivity;
import com.tagtracer.databinding.FragmentCyclecountBinding;
import com.tagtracer.rfid.reader.IRFIDReader;
import com.tagtracer.util.IDisposable;
import com.tagtracer.util.callbacks.IKeyEventCallback;

public class CycleCountFragment extends Fragment implements IDisposable, IKeyEventCallback {
    public static final String TAG = CycleCountFragment.class.getName();
    private FragmentCyclecountBinding binding;
    private IRFIDReader reader;
    private boolean isReading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.reader = ((MainActivity) requireActivity()).getReader();

        // Set the configuration
        this.initializeConfiguration(30, 0, 0);

        // Set the key event callbacks
        ((MainActivity) requireActivity()).setKeyEventCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentCyclecountBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.dispose();
    }

    @Override
    public void dispose() {
        this.binding = null;
    }

    @Override
    public void onKeyUp() {
        try {
            this.isReading = !this.reader.stopInventory();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Toast.makeText(requireContext(), "failed to stop inventory", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onKeyDown() {
        if (!this.isReading) {
            try {
                this.isReading = this.reader.startInventory();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                Toast.makeText(requireContext(), "failed to start inventory", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeConfiguration(int powerLevel, int sessionId, int target) {
        if (this.reader != null) {
            try {
                this.reader.setPowerLevel(powerLevel);
                this.reader.setSession(sessionId, target);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                Toast.makeText(requireContext(), "failed to apply configuration", Toast.LENGTH_LONG).show();
            }
        }
    }
}
