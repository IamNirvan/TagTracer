package com.tagtracer.fragments.loading;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tagtracer.MainActivity;
import com.tagtracer.databinding.FragmentLoadingBinding;
import com.tagtracer.fragments.cyclecount.CycleCountFragment;
import com.tagtracer.util.IDisposable;

public class LoadingFragmentV2 extends Fragment implements IDisposable {
    public static final String TAG = LoadingFragmentV2.class.getName();
    private FragmentLoadingBinding binding;
    private ProgressBar progressBar;
    private Handler handler;

    private void handleLoading() {
        Runnable runnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress <= 100) {
                    progressBar.setProgress(progress++);
                    progress += 10;
                    handler.postDelayed(this, 100);
                }
                else {
                    final CycleCountFragment fragment = new CycleCountFragment();
                    ((MainActivity) requireActivity()).loadFragment(fragment, false, CycleCountFragment.TAG);
                }
            }
        };
        handler.post(runnable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentLoadingBinding.inflate(inflater, container, false);
        this.handler = new Handler(Looper.getMainLooper());
        this.progressBar = this.binding.progressBar;
        return this.binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.handleLoading();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        dispose();
    }

    @Override
    public void dispose() {
        this.binding = null;
    }
}
