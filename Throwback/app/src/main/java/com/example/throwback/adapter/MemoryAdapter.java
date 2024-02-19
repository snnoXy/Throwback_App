package com.example.throwback.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.throwback.object.Throwback;
import com.example.throwback.databinding.RecyclerRowBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryHolder>{


    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;


    private ArrayList<Throwback> throwbackArrayList;

    public MemoryAdapter(ArrayList<Throwback> throwbackArrayList) {
        this.throwbackArrayList = throwbackArrayList;
    }

    @NonNull
    @Override
    public MemoryAdapter.MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        return new MemoryHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryAdapter.MemoryHolder holder, int position) {


        if (!throwbackArrayList.isEmpty() && position >= 0 && position < throwbackArrayList.size()){

            holder.binding.titleText.setText(throwbackArrayList.get(position).title);
            holder.binding.dateText.setText(throwbackArrayList.get(position).date);
            holder.binding.descriptionText.setText(throwbackArrayList.get(position).description);
            Glide.with(holder.itemView.getContext())
                    .load(throwbackArrayList.get(position).downloadUrl)
                    .into(holder.binding.imageView);

            holder.binding.customizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Delete Confirmation");
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            firebaseStorage.getReference().child("images/"+throwbackArrayList.get(position).uid+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(view.getContext(), "Image deleted.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                            firebaseFirestore.collection("Throwbacks").whereEqualTo("title",throwbackArrayList.get(position).title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentId = documentSnapshot.getId();

                                        firebaseFirestore.collection("Throwbacks").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                notifyDataSetChanged();
                                                System.out.println(throwbackArrayList.size());
                                                Toast.makeText(view.getContext(), "Throwback deleted.",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(view.getContext(), e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });


                        }
                    });

                    builder.setNegativeButton("No",null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }
            });
        }
        else{

            Toast.makeText(holder.itemView.getContext(), "Out of bond",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public int getItemCount() {
        return throwbackArrayList.size();
    }

    public class MemoryHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding binding;
        public MemoryHolder(@NonNull RecyclerRowBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
