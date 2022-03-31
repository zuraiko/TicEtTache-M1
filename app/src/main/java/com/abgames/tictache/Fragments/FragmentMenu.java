package com.abgames.tictache.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.abgames.tictache.Activity.LoginActivity;
import com.abgames.tictache.Activity.MainActivity;
import com.abgames.tictache.R;
import com.google.android.material.navigation.NavigationView;

import javax.annotation.Nullable;


/**
 * Fragment to manage main menu
 * It allow to refresh the list of ttodo
 * also, we can disconnect from this menu
 */
public class FragmentMenu extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container,
                false);
        NavigationView vNavigation = view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getGroupId() == 0)
                //Update MainActivity
                startActivity( new Intent(view.getContext(), MainActivity.class));
            else{
                //Disconnect
                startActivity( new Intent(view.getContext(), LoginActivity.class));
            }

            return false;
        });
        return  view ;
    }


}
