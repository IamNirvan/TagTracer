package com.tagtracer.fragments.cyclecount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.tagtracer.viewmodels.cyclecount.CycleCountViewModel;

import java.util.ArrayList;

public class CycleCountFragment extends Fragment implements IDisposable, IKeyEventCallback {
    public static final String TAG = CycleCountFragment.class.getName();
    private FragmentCyclecountBinding binding;
    private IRFIDReader reader;
    private boolean isReading;
    private TagConsumer consumer;
    private CycleCountViewModel viewModel;
    private RFIDTagRecyclerViewAdaptor rfidAdaptor;
    private TextView counter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.reader = ((MainActivity) requireActivity()).getReader();
        this.viewModel = new ViewModelProvider(requireActivity()).get(CycleCountViewModel.class);
        this.initializeConfiguration(30, 0, 0);
        ((MainActivity) requireActivity()).setKeyEventCallback(this);
        consumer = new TagConsumer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentCyclecountBinding.inflate(inflater, container, false);

        // Set up the recycler view
        RecyclerView recyclerView = this.binding.rvScannedItems;
        rfidAdaptor = new RFIDTagRecyclerViewAdaptor(requireContext(), this.viewModel.getTags());
        recyclerView.setAdapter(rfidAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up the counter
        counter = this.binding.count;
        counter.setText(String.valueOf(this.viewModel.getTags().size()));

        // Set up the observers
        this.viewModel.tagSet.observe(getViewLifecycleOwner(), this::updateRecyclerView);
        this.viewModel.counter.observe(getViewLifecycleOwner(), this::updateScannedCount);

        return this.binding.getRoot();
    }

    private void updateRecyclerView(ArrayList<RFIDTag> newTags) {
        this.rfidAdaptor.submitList(newTags);
    }

    private void updateScannedCount(int count) {
        this.counter.setText(String.valueOf(count));
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

    private void startTagRead() throws RuntimeException {
        if (this.reader == null) {
            throw new RuntimeException("reader is null");
        }

        Runnable consumerRunnable = this.consumer.getConsumer(
            this.reader.getQueue(),
            tag -> {
                this.viewModel.addScannedTag(tag);
            }
        );

        this.consumer.startConsumer(consumerRunnable);
        this.isReading = this.reader.startInventory();
    }

    private void stopTagRead() {
        this.consumer.stopConsumer();
        this.isReading = !this.reader.stopInventory();
    }

    @Override
    public void onKeyUp() {
        try {
            this.stopTagRead();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Toast.makeText(requireContext(), "failed to stop inventory", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onKeyDown() {
        if (!this.isReading) {
            try {
                this.startTagRead();
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
