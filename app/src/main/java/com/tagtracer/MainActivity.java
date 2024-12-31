package com.example.tagtracer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.tagtracer.fragments.loading.LoadingFragmentV2;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadFragment(new LoadingFragmentV2(), false, LoadingFragmentV2.TAG);
        }
    }

    public void loadFragment(@NotNull Fragment fragment, boolean addToBackStack, String name) {
        FragmentManager fManager = getSupportFragmentManager();

        if (!addToBackStack) {
            fManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            fManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(name)
                    .commit();
        }
    }
}