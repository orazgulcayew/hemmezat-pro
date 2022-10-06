package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gocreative.tm.hemmezatproadmin.Adapters.AddAdvertiseAdapter;
import com.gocreative.tm.hemmezatproadmin.Models.Image;
import com.gocreative.tm.hemmezatproadmin.MultipleImageUpload.CoreHelper;
import com.gocreative.tm.hemmezatproadmin.MultipleImageUpload.ImagesAdapter;
import com.gocreative.tm.hemmezatproadmin.MultipleImageUpload.UploadModel;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddAdvertiseFragment extends Fragment {
    List<String> urls;
    AddAdvertiseAdapter addAdvertiseAdapter;

    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
    RecyclerView recyclerView, newItemsRecyclerView;
    List<UploadModel> imagesList;
    List<String> savedImagesUri;
    List<Image> imageList;
    List<Image> newImageList;

    ImagesAdapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    DocumentReference reference;
    Button btnPickImages, btnUploadImages;
    Map<String, Object> dataMap;
    int counter;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_advertise, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Biraz garaşyň...");
        progressDialog.show();

        imageList = new ArrayList<>();
        urls = new ArrayList<>();
        newImageList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = firestore.collection("admin").document("reklamalar");

        savedImagesUri = new ArrayList<>();

        btnPickImages = view.findViewById(R.id.pick_image);
        btnUploadImages = view.findViewById(R.id.accept);

        imagesList = new ArrayList<>();
        coreHelper = new CoreHelper(getContext());

        //Code to show list of images start
        newItemsRecyclerView = view.findViewById(R.id.newItemsRecyclerView);
        adapter = new ImagesAdapter(getContext(), imagesList);
        newItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        newItemsRecyclerView.setHasFixedSize(true);
        newItemsRecyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        //Code to show list of images end
        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissionAndPickImage();
            }
        });

        btnUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImages(view);
            }
        });

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    progressDialog.dismiss();
                    urls = (List<String>) documentSnapshot.get("images");

                    for (String url: urls){
                        imageList.add(new Image(url));
                    }
                    addAdvertiseAdapter = new AddAdvertiseAdapter(getContext(), imageList);
                    recyclerView.setAdapter(addAdvertiseAdapter);
                    addAdvertiseAdapter.notifyDataSetChanged();

                }else{
                    progressDialog.dismiss();
                    Toasty.error(getContext(), "Nasazlyk doredi!", Toasty.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("GET", "onFailure: " + e.getMessage());
                progressDialog.dismiss();

            }
        });



        return view;
    }
    private void uploadImages(View view){
        if (adapter.getItemCount() != 0){

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Ýüklenýär 0/"+imagesList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            if (imagesList.size() == 0){
                saveImageDataToFirestore(progressDialog);
            }
            for (int i = 0; i < imagesList.size(); i++) {
                final int finalI = i;

                storageReference.child("admin/").child("reklamalar/").child(imagesList.get(i).getImageName()).putFile(imagesList.get(i).getImageUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageReference.child("admin/").child("reklamalar/").child(imagesList.get(finalI).getImageName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    counter++;
                                    progressDialog.setMessage("Ýüklendi "+counter+"/"+imagesList.size());
                                    if (task.isSuccessful()){
                                        savedImagesUri.add(task.getResult().toString());
                                    }else{
                                        storageReference.child("admin/").child("reklamalar/").child(imagesList.get(finalI).getImageName()).delete();
                                    }
                                    if (counter == imagesList.size()){
                                        saveImageDataToFirestore(progressDialog);
                                    }
                                }
                            });
                        }else{
                            progressDialog.setMessage("Ýüklenýär "+counter+"/"+imagesList.size());
                            counter++;
                            Toasty.error(getActivity(), "Suratlary ýükläp bolmady"+imagesList.get(finalI).getImageName(), Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else{
            Uri imageUri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getActivity().getResources().getResourcePackageName(R.drawable.ic_image))
                    .appendPath(getActivity().getResources().getResourceTypeName(R.drawable.ic_image))
                    .appendPath(getActivity().getResources().getResourceEntryName(R.drawable.ic_image))
                    .build();

            imagesList.add(new UploadModel("default", imageUri));
            uploadImages(view);
        }
    }

    private void saveImageDataToFirestore(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Ýüklenen suratlar ýatda saklanýar...");

        // Add old images
        for (int i=0; i<imageList.size(); i++){
            savedImagesUri.add(imageList.get(i).getImageUrl());
        }


        dataMap = new HashMap<>();
        //Below line of code will put your images list as an array in firestore
        dataMap.put("images", savedImagesUri);

        //Below commented code is no more recommended..!
        /*for (int i = 0; i < savedImagesUri.size(); i++) {
            dataMap.put("image" + i, savedImagesUri.get(i));
        }*/
        reference.set(dataMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("Images", "onComplete: " + savedImagesUri);
                    progressDialog.dismiss();
                    coreHelper.createAlert("Üstünlikli!", "Surat üstünlikli ýüklendi", "Bolýa", "", null, null, null);
                    HomeFragment homeFragment = new HomeFragment();

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }

                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fragment_container, homeFragment)
                            .commit();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toasty.error(getActivity(), "Näsazlyk döredi!", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity:SaveData", e.getMessage());
            }
        });
    }


    private void verifyPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //If permission is granted
                pickImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            //no need to check permissions in android versions lower then marshmallow
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toasty.warning(getContext(), "Rugsat berilmedik!", Toasty.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ClipData clipData = data.getClipData();

                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            adapter.notifyDataSetChanged();
                            imagesList.add(new UploadModel(coreHelper.getFileNameFromUri(uri), uri));
                        }
                    } else {
                        Uri uri = data.getData();
                        imagesList.add(new UploadModel(coreHelper.getFileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                    }
                }
        }


    }

}