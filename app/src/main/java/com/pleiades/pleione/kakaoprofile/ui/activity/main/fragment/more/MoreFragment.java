package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.more;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kakaoprofile.R;

public class MoreFragment extends Fragment {
    private Context context;

    private RecyclerView defaultRecyclerView, infoRecyclerView, applicationRecyclerView;
    private String[] defaultList, infoList, applicationList;
    private MoreRecyclerAdapter defaultAdapter, infoAdapter, applicationAdapter;

    // root view
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_more, container, false);

        initializeRecyclerView();
        refreshRecyclerView();

        return root;
    }

    private void initializeRecyclerView(){
        LinearLayoutManager linearLayoutManager;

        // default
        defaultRecyclerView = root.findViewById(R.id.more_recycler_view_default);
        defaultRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        defaultRecyclerView.setLayoutManager(linearLayoutManager);

        // info
        infoRecyclerView = root.findViewById(R.id.more_recycler_view_info);
        infoRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        infoRecyclerView.setLayoutManager(linearLayoutManager);

        // application
        applicationRecyclerView = root.findViewById(R.id.more_recycler_view_application);
        applicationRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        applicationRecyclerView.setLayoutManager(linearLayoutManager);

        initializeList();
    }

    private void initializeList(){
        defaultList = context.getResources().getStringArray(R.array.more_default);
        infoList = context.getResources().getStringArray(R.array.more_info);
        applicationList = context.getResources().getStringArray(R.array.more_application);
    }

    private void refreshRecyclerView(){
        // default
        defaultAdapter = new MoreRecyclerAdapter(defaultList);
        defaultAdapter.setHasStableIds(true);
        defaultRecyclerView.setAdapter(defaultAdapter);

        // info
        infoAdapter = new MoreRecyclerAdapter(infoList);
        infoAdapter.setHasStableIds(true);
        infoRecyclerView.setAdapter(infoAdapter);

        // application
        applicationAdapter = new MoreRecyclerAdapter(applicationList);
        applicationAdapter.setHasStableIds(true);
        applicationRecyclerView.setAdapter(applicationAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
