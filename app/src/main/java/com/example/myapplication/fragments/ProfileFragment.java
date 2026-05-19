package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.profile.AddressActivity;
import com.example.myapplication.profile.MyItemsActivity;
import com.example.myapplication.profile.NotificationActivity;
import com.example.myapplication.profile.SavedItemsActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.menu_barang_saya).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MyItemsActivity.class)));

        view.findViewById(R.id.menu_barang_tersimpan).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SavedItemsActivity.class)));

        view.findViewById(R.id.menu_alamat).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddressActivity.class)));

        view.findViewById(R.id.menu_notifikasi).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationActivity.class)));
    }
}
