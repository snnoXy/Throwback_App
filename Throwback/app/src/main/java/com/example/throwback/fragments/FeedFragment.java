package com.example.throwback.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.throwback.activities.AuthActivity;
import com.example.throwback.LoadingDialog;
import com.example.throwback.R;
import com.example.throwback.adapter.MemoryAdapter;
import com.example.throwback.databinding.FragmentFeedBinding;
import com.example.throwback.object.Throwback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class FeedFragment extends Fragment {


    private FragmentFeedBinding binding;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private MemoryAdapter memoryAdapter;
    private LoadingDialog loadingDialog;

    private ArrayList<Throwback> throwbackArrayList;


    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        throwbackArrayList = new ArrayList<>();

        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        memoryAdapter = new MemoryAdapter(throwbackArrayList);
        binding.recyclerView.setAdapter(memoryAdapter);

        if (throwbackArrayList.isEmpty()) {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.emptyText.setVisibility(View.VISIBLE);
        } else {
            binding.animationView.setVisibility(View.GONE);
            binding.emptyText.setVisibility(View.GONE);
        }

        memoryAdapter.notifyDataSetChanged();

        binding.optionsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    PopupMenu popup = new PopupMenu(requireContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.actions, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if(menuItem.getItemId() == R.id.newthrowback){
                                NavDirections action = FeedFragmentDirections.actionFeedFragmentToNewThrowbackFragment();
                                Navigation.findNavController(view).navigate(action);
                            }
                            else if (menuItem.getItemId() == R.id.logout){

                                auth.signOut();
                                Intent intentToLogin = new Intent(getActivity(), AuthActivity.class);
                                startActivity(intentToLogin);
                                getActivity().finish();
                            }
                            return true;
                        }
                    });

                popup.show();




            }

        });
    }

    private void getData(){

        loadingDialog.show();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadingDialog.cancel();
            }
        };
        handler.postDelayed(runnable,2000);

        firebaseFirestore.collection("Throwbacks").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("userEmail",auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error !=null){
                    System.out.println(error.getLocalizedMessage());
                }

                if(value !=null){
                    throwbackArrayList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String,Object> data = snapshot.getData();

                        String userEmail = (String) data.get("userEmail");
                        String description = (String) data.get("description");
                        String downloadUrl = (String) data.get("downloadUrl");
                        String title = (String) data.get("title");
                        String uid = (String) data.get("uid").toString();


                        Timestamp date = (Timestamp) data.get("date");
                        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

                        String strDate ="null";
                        if(date !=null){
                            strDate = dateFormat.format(date.toDate());
                        }

                        Throwback throwback = new Throwback(userEmail,description,downloadUrl,title,strDate,uid);
                        throwbackArrayList.add(throwback);

                    }


                }

                if (throwbackArrayList.isEmpty()) {
                    binding.animationView.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                } else {
                    binding.animationView.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.GONE);
                }
                memoryAdapter.notifyDataSetChanged();

            }
        });

    }
}