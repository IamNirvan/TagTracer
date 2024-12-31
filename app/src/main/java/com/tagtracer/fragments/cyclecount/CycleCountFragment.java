package com.tagtracer.fragments.cyclecount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tagtracer.MainActivity;
import com.tagtracer.databinding.FragmentCyclecountBinding;
import com.tagtracer.models.RFIDTag;
import com.tagtracer.rfid.IRFIDReader;
import com.tagtracer.util.IDisposable;
import com.tagtracer.util.TagConsumer;
import com.tagtracer.util.adaptors.RFIDTagRecyclerViewAdaptor;
import com.tagtracer.util.callbacks.IKeyEventCallback;

import java.util.ArrayList;

public class CycleCountFragment extends Fragment implements IDisposable, IKeyEventCallback {
    public static final String TAG = CycleCountFragment.class.getName();
    private FragmentCyclecountBinding binding;
    private IRFIDReader reader;
    private boolean isReading;
    private ArrayList<RFIDTag> tags;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.reader = ((MainActivity) requireActivity()).getReader();

        // Set the configuration
        this.initializeConfiguration(30, 0, 0);

        // Set the key event callbacks
        ((MainActivity) requireActivity()).setKeyEventCallback(this);

        // TODO: remove this after testing
        this.tags = this.generateMockTags(10);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentCyclecountBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = this.binding.rvScannedItems;
        RFIDTagRecyclerViewAdaptor adaptor = new RFIDTagRecyclerViewAdaptor(requireContext(), this.tags);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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

    private ArrayList<RFIDTag> generateMockTags(int count) {
        final ArrayList<RFIDTag> response = new ArrayList<>();
        for(int x = 0; x < count; x++) {
            response.add(new RFIDTag(String.format("tid-%s", x), String.format("rssi-%s", x)));
        }
        return response;
    }

}
