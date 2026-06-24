package com.example.diplomskiandroid.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.diplomskiandroid.fragments.HomeFragment;
import com.example.diplomskiandroid.fragments.ProfileFragment;
import com.example.diplomskiandroid.fragments.RequestsFragment;
import com.example.diplomskiandroid.fragments.VehiclesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {

            case 0:
                return new HomeFragment();

            case 1:
                return new VehiclesFragment();

            case 2:
                return new RequestsFragment();

            case 3:
                return new ProfileFragment();

            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}