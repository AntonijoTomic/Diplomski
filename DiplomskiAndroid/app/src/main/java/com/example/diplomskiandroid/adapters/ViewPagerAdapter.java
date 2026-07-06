package com.example.diplomskiandroid.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.diplomskiandroid.fragments.AdminDashboardFragment;
import com.example.diplomskiandroid.fragments.AdminRequestsFragment;
import com.example.diplomskiandroid.fragments.HomeFragment;
import com.example.diplomskiandroid.fragments.ProfileFragment;
import com.example.diplomskiandroid.fragments.RequestsFragment;
import com.example.diplomskiandroid.fragments.VehiclesFragment;
import com.example.diplomskiandroid.fragments.WorkOrdersFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

        private final String role;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String role) {
            super(fragmentActivity);
            this.role = role;
        }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isAdmin = role.equals("ADMIN") || role.equals("SERVICER");

        if (isAdmin) {
            switch (position) {
                case 0:
                    return new AdminDashboardFragment();
                case 1:
                    return new AdminRequestsFragment();
                case 2:
                    return new WorkOrdersFragment();
                case 3:
                    return new ProfileFragment();
                default:
                    return new AdminDashboardFragment();
            }
        }

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