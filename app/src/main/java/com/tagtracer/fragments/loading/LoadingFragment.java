package com.tagtracer.fragments.loading;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tagtracer.R;
import com.tagtracer.databinding.FragmentLoadingBinding;
import com.tagtracer.util.IDisposable;

public class LoadingFragment extends Fragment implements IDisposable {
    private static final String TAG = LoadingFragment.class.getName();
    private FragmentLoadingBinding binding;
    private ImageView appLogo;
    private Animation pulsateAnimation;
    private boolean isAnimationRunning;

    private void startAnimation() {
        if (!this.isAnimationRunning ) {
            if (appLogo != null && pulsateAnimation != null) {
                Log.d(TAG, "startAnimation: starting animation");
                this.appLogo.startAnimation(this.pulsateAnimation);
                this.isAnimationRunning = true;
            } else {
                Log.w(TAG, "startAnimation: cannot start animation since required resource(s) are null");
            }
        }
    }

    private void stopAnimation() {
        if (this.isAnimationRunning) {
            if (appLogo != null && pulsateAnimation != null) {
                Log.d(TAG, "stopAnimation: stopping animation");
                this.appLogo.clearAnimation();
                this.isAnimationRunning = false;
            } else {
                Log.w(TAG, "startAnimation: cannot stop animation since required resource(s) are null");
            }
        }
    }

    private void handleLoading() {
        this.startAnimation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentLoadingBinding.inflate(inflater, container, false);

        this.appLogo = binding.appLogo;
        this.pulsateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.pulsate);

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
        stopAnimation();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        stopAnimation();
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
        this.stopAnimation();
    }
}
