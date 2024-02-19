package com.example.throwback.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.throwback.LoadingDialog;
import com.example.throwback.activities.MainActivity;
import com.example.throwback.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupFragment extends Fragment {

    private FirebaseAuth auth;

    private FragmentSignupBinding binding;
    LoadingDialog loadingDialog;


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null){
            //Go MainActivity
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignupBinding.inflate(inflater,container,false);

        loadingDialog = new LoadingDialog(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog.show();

                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String passwordAgain = binding.password2EditText.getText().toString();

                System.out.println(password);
                System.out.println(passwordAgain);
                if(email.equals("") || password.equals("")){
                    Toast.makeText(requireContext(),"Please enter email and password!",Toast.LENGTH_LONG).show();
                    loadingDialog.cancel();
                }
                else if (!password.equals(passwordAgain)) {
                    Toast.makeText(requireContext(),"Passwords does not match!",Toast.LENGTH_LONG).show();
                    loadingDialog.cancel();
                }
                else{

                    auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            loadingDialog.cancel();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            loadingDialog.cancel();

                            Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

                }

            }
        });




    }
}