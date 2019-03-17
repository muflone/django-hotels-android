package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Structure;

public class StructuresFragment extends Fragment {
    private View rootLayout;
    private TabLayout structuresTabs;
    private TabLayout buildingsTabs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ApiData apiData = Singleton.getInstance().apiData;
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.structures_fragment, container, false);
        // Initialize structuresTabs
        this.structuresTabs = this.rootLayout.findViewById(R.id.structuresLayout);
        this.structuresTabs.removeAllTabs();
        for (Structure structure : apiData.structures) {
            TabLayout.Tab tabItem = this.structuresTabs.newTab();
            tabItem.setText(structure.name);
            this.structuresTabs.addTab(tabItem);
        }
        this.structuresTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBuildingsTabs(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Initialize buildingsTabs
        this.buildingsTabs = rootLayout.findViewById(R.id.buildingsLayout);
        this.buildingsTabs.removeAllTabs();
        this.buildingsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // viewPager.setCurrentItem(tab.getPosition());
                System.out.println(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Select the first structure tab
        this.loadBuildingsTabs(this.structuresTabs.getTabAt(0));

        return rootLayout;
    }

    protected void loadBuildingsTabs(TabLayout.Tab tab) {
        // Load buildings tabs from the selected Structure tab
        ApiData apiData = Singleton.getInstance().apiData;
        this.buildingsTabs.removeAllTabs();
        int position = this.structuresTabs.getSelectedTabPosition();
        Structure selectedStructure = apiData.structures.get(position);
        for (Building building : selectedStructure.buildings) {
            TabLayout.Tab tabItem = this.buildingsTabs.newTab();
            tabItem.setText(building.name.replace(
                    String.format("%s - ", selectedStructure.name),
                    "").replace(
                            String.format("%s ", selectedStructure.name),
                            ""));
            this.buildingsTabs.addTab(tabItem);
        }
    }
}
