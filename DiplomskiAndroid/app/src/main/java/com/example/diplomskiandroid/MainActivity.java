package com.example.diplomskiandroid;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplomskiandroid.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private String role;
    private String requestStatusFilter;
    private boolean openingFilteredRequests = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        SharedPreferences preferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        role = preferences.getString("role", "USER");

        setupNavigationLabels();

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, role);
        viewPager.setAdapter(adapter);


        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            }

            if (id == R.id.nav_vehicles) {

                boolean isAdmin =
                        role.equals("ADMIN")
                                || role.equals("SERVICER");

                if (isAdmin) {

                    if (!openingFilteredRequests) {
                        requestStatusFilter = null;
                    }

                    openingFilteredRequests = false;
                }

                viewPager.setCurrentItem(1);
                return true;
            }

            if (id == R.id.nav_requests) {
                viewPager.setCurrentItem(2);
                return true;
            }

            if (id == R.id.nav_profile) {
                viewPager.setCurrentItem(3);
                return true;
            }

            return false;
        });

        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {

                        switch (position) {

                            case 0:
                                bottomNavigation.setSelectedItemId(R.id.nav_home);
                                break;

                            case 1:
                                bottomNavigation.setSelectedItemId(R.id.nav_vehicles);
                                break;

                            case 2:
                                bottomNavigation.setSelectedItemId(R.id.nav_requests);
                                break;

                            case 3:
                                bottomNavigation.setSelectedItemId(R.id.nav_profile);
                                break;
                        }
                    }
                }
        );
    }

    private void setupNavigationLabels() {boolean isAdmin = role.equals("ADMIN") || role.equals("SERVICER");

        if (isAdmin) {
            bottomNavigation.getMenu().findItem(R.id.nav_home).setTitle("Dashboard");
            bottomNavigation.getMenu().findItem(R.id.nav_vehicles).setTitle("Zahtjevi");
            bottomNavigation.getMenu().findItem(R.id.nav_requests).setTitle("Nalozi");
            bottomNavigation.getMenu().findItem(R.id.nav_profile).setTitle("Profil");
        }
    }
    public void openAdminPage(int position) {
        viewPager.setCurrentItem(position, true);
    }
    public void openAdminRequests(String status) {
        requestStatusFilter = status;
        openingFilteredRequests = status != null;
        viewPager.setCurrentItem(1, true);
    }

    public String getRequestStatusFilter() {
        return requestStatusFilter;
    }
}
