package com.muflone.android.django_hotels.fragments;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.models.Building;
import com.muflone.android.django_hotels.database.models.Room;
import com.muflone.android.django_hotels.database.models.Structure;

import java.util.ArrayList;

public class StructuresFragment extends Fragment {
    private View rootLayout;
    private TabLayout structuresTabs;
    private TabLayout buildingsTabs;
    private ListView roomsView;

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
                loadRooms(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Select the first structure tab
        this.roomsView = rootLayout.findViewById(R.id.roomsView);
        this.loadBuildingsTabs(this.structuresTabs.getTabAt(0));
        return rootLayout;
    }

    protected void loadBuildingsTabs(TabLayout.Tab tab) {
        // Load buildings tabs for the selected Structure tab
        ApiData apiData = Singleton.getInstance().apiData;
        this.buildingsTabs.removeAllTabs();
        int position = this.structuresTabs.getSelectedTabPosition();
        if (position >= 0) {
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

    protected void loadRooms(TabLayout.Tab tab) {
        // Load rooms for the selected Building tab
        /*
        ArrayList<Room> data = new ArrayList(
                Singleton.getInstance().apiData.structures
                        .get(structuresTabs.getSelectedTabPosition()).buildings
                        .get(buildingsTabs.getSelectedTabPosition()).rooms);
        this.roomsView.setAdapter(new CustomAdapter(data, getActivity()));
        */
        ArrayList<String> data = new ArrayList();
        for (Room room : Singleton.getInstance().apiData.structures
                .get(structuresTabs.getSelectedTabPosition()).buildings
                .get(buildingsTabs.getSelectedTabPosition()).rooms) {
            data.add(room.name);
        }
        this.roomsView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data));
        this.roomsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("testy", "I Clicked on Row " + position + " and it worked!");
            }
        });
    }
/*
    public class CustomAdapter extends ArrayAdapter<Room> {
        private ArrayList<Room> dataSet;
        private ViewHolder viewHolder;
        private LayoutInflater inflater;

        // View lookup cache
        private class ViewHolder {
            TextView textRoomName;
            Button buttonSetWorker;
        }

        public CustomAdapter(ArrayList<Room> data, Context context) {
            super(context, R.layout.structures_rooms_content, data);
            this.dataSet = data;
            this.inflater = LayoutInflater.from(context);
            this.viewHolder = new ViewHolder();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            // Get the data item for this position
            final Room room = getItem(position);
            convertView = this.inflater.inflate(R.layout.structures_rooms_content,
                    parent, false);
            this.viewHolder.textRoomName = convertView.findViewById(R.id.textRoomName);
            this.viewHolder.textRoomName.setText(room.name);
            // this.viewHolder.buttonSetWorker = convertView.findViewById(R.id.buttonSetWorker);
            // this.viewHolder.buttonSetWorker.setText(String.valueOf(room.id));
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.d("AA", "string: " + room.name);
                    Log.d("AA", "int: " + room.id);
                }
            });
            return convertView;
        }
    }
*/
}
