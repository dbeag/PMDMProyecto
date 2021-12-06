package com.example.proyecto.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.proyecto.MapsFragment;
import com.example.proyecto.ProfileFragment;

public class MainAdapter extends FragmentStateAdapter {
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MainAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MainAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new MapsFragment();
        }
        return new ProfileFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
