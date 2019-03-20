package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.ArrayList;
import java.util.List;

public class StructuresFragment extends Fragment {
    private View rootLayout;
    private TabLayout structuresTabs;
    private ListView employeesView;
    private List<String> employeesList = new ArrayList<>();

    private ListView buildingsView;
    private List<String> buildingsList = new ArrayList<>();

    private ListView roomsView;
    private List<String> roomsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ApiData apiData = Singleton.getInstance().apiData;
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.structures_fragment, container, false);
        this.structuresTabs = rootLayout.findViewById(R.id.structuresLayout);

        this.employeesView = rootLayout.findViewById(R.id.employeesView);
        this.employeesView.setAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, this.employeesList));

        this.buildingsView = rootLayout.findViewById(R.id.buildingsView);
        this.buildingsView.setAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, this.buildingsList));
        this.buildingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("testy", "I Clicked on Row " + position + " and it worked!");
                view.setSelected(true);
            }
        });

        this.roomsView = rootLayout.findViewById(R.id.roomsView);
        this.roomsView.setAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, this.roomsList));

        // Initialize Structures
        this.structuresTabs.removeAllTabs();
        for (Structure structure : apiData.structures) {
            TabLayout.Tab tabItem = this.structuresTabs.newTab();
            tabItem.setText(structure.name);
            this.structuresTabs.addTab(tabItem);
        }
        this.structuresTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBuildings(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Select the first structure tab
        this.loadBuildings(this.structuresTabs.getTabAt(0));
        this.buildingsView.requestFocusFromTouch();
        this.buildingsView.setSelection(0);

        return rootLayout;
    }

    protected void loadBuildings(TabLayout.Tab tab) {
        // Load buildings tabs for the selected Structure tab
        buildingsList.clear();
        ApiData apiData = Singleton.getInstance().apiData;
        int position = this.structuresTabs.getSelectedTabPosition();
        if (position >= 0) {
            Structure selectedStructure = apiData.structures.get(position);
            for (Building building : selectedStructure.buildings) {
                buildingsList.add(building.name.replace(
                        String.format("%s - ", selectedStructure.name),
                        "").replace(
                        String.format("%s ", selectedStructure.name),
                        ""));
            }
        }
        // Update data
        ((ArrayAdapter) buildingsView.getAdapter()).notifyDataSetChanged();
    }

    protected void loadRooms(TabLayout.Tab tab) {
        // Load rooms for the selected Building tab
        /*
        ArrayList<String> data = new ArrayList();
        for (Room room : this.roomsList.get(tab.getPosition())) {
            data.add(room.name);
        }
        */
        this.roomsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("testy", "I Clicked on Row " + position + " and it worked!");
            }
        });
    }
}
