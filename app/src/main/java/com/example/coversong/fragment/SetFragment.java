package com.example.coversong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import oss.main.LoginActivity;
import oss.main.R;

/**
 */
public class SetFragment extends Fragment {

    public SetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_set, container, false);
        rootView.findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            GoogleSignIn
                    .getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                    .signOut()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent login_intent = new Intent(getContext(), LoginActivity.class);
                            login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(login_intent);
                        }
                    });
        });
        return rootView;
    }
}