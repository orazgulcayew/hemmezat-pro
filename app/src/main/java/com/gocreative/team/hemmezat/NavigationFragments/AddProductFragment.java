package com.gocreative.team.hemmezat.NavigationFragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.team.hemmezat.MultipleImageUpload.CoreHelper;
import com.gocreative.team.hemmezat.MultipleImageUpload.ImagesAdapter;
import com.gocreative.team.hemmezat.MultipleImageUpload.UploadModel;
import com.gocreative.tm.hemmezat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class AddProductFragment extends Fragment {
    Spinner locationView, typeV, currencyV;
    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
    RecyclerView recyclerView;
    List<UploadModel> imagesList;
    List<String> savedImagesUri;
    ImagesAdapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    DocumentReference reference;
    Button btnPickImages, btnUploadImages;
    int counter;
    TextView selectedCategoryView;
    EditText nameView, priceView, explanationView;
    String name, price, explanation;
    LinearLayout categorySelect;
    String categoryName, subCategoryName, currentUid, location, phoneNumber, type, currency;
    Map<String, Object> dataMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        locationView = view.findViewById(R.id.location);
        typeV = view.findViewById(R.id.product_type);
        nameView = view.findViewById(R.id.product_name);
        priceView = view.findViewById(R.id.product_price);
        explanationView = view.findViewById(R.id.product_info);
        currencyV = view.findViewById(R.id.currencies_list);

        FirebaseAuth user = FirebaseAuth.getInstance();
        currentUid = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = firestore.collection("users").document(currentUid).collection("user_products").document();


        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, getResources().getStringArray(R.array.location));
        locationsAdapter.setDropDownViewResource(R.layout.spinner_text);
        locationView.setAdapter(locationsAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, getResources().getStringArray(R.array.product_type));
        typeAdapter.setDropDownViewResource(R.layout.spinner_text);
        typeV.setAdapter(typeAdapter);

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, getResources().getStringArray(R.array.currencies));
        currencyAdapter.setDropDownViewResource(R.layout.spinner_text);
        currencyV.setAdapter(currencyAdapter);

        categoryName = getArguments().getString("categoryName");
        subCategoryName = getArguments().getString("subCategoryName");
        selectedCategoryView = view.findViewById(R.id.selected_category);

        if (!categoryName.equals("null") && !subCategoryName.equals("null")){
            selectedCategoryView.setTextColor(Color.BLACK);
            selectedCategoryView.setText(categoryName + "/" + "\n" + subCategoryName);
            DocumentReference reference = firestore.collection("users").document(currentUid);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        phoneNumber = task.getResult().getString("phone_number");
                    }
                }
            });
        }

        categorySelect = view.findViewById(R.id.select_category);
        savedImagesUri = new ArrayList<>();

        btnPickImages = view.findViewById(R.id.pick_image);
        btnUploadImages = view.findViewById(R.id.accept);
        imagesList = new ArrayList<>();
        coreHelper = new CoreHelper(getContext());
        //Code to show list of images start
        recyclerView = view.findViewById(R.id.recyclerViewAddProduct);
        adapter = new ImagesAdapter(getContext(), imagesList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        // Go to category fragment
        categorySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategorySelectFragment categorySelectFragment = new CategorySelectFragment();
                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, categorySelectFragment)
                        .addToBackStack(AddProductFragment.class.getSimpleName())
                        .commit();
            }
        });

        if (categoryName.equals("null") && subCategoryName.equals("null")){
            nameView.setEnabled(false);
            priceView.setEnabled(false);
            explanationView.setEnabled(false);
        }else{
            nameView.setEnabled(true);
            priceView.setEnabled(true);
            explanationView.setEnabled(true);
        }

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
                name = nameView.getText().toString();
                price = priceView.getText().toString();
                explanation = explanationView.getText().toString();
                location = locationView.getSelectedItem().toString();
                type = typeV.getSelectedItem().toString();
                currency = currencyV.getSelectedItem().toString();
                if (everythingOk(name, price, explanation, location, type)){
                    uploadImages(view);
                }else{
                    Toasty.warning(getContext(), "Maglumatlary doly giriziň!", Toasty.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean everythingOk(String type, String name, String price, String explanation, String location) {
        return !name.isEmpty()
                && !price.isEmpty()
                && !explanation.isEmpty()
                && !location.equals("Saýlanmadyk")
                && !categoryName.equals("null")
                && !subCategoryName.equals("null")
                && !type.equals("Saýlanmadyk");

    }
//    private Uri compress(Uri imageUri){
//        if (imageUri != null){
//            File thumbFile = new File(imageUri.getPath());
//            Bitmap thumbBitmap = new Compressor(getActivity())
//                        .setMaxHeight(200)
//                        .setMaxWidth(200)
//                        .setQuality(50)
//                        .compressToBitmap(thumbFile);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            // thumb image
//            thumbByte = baos.toByteArray();
//        }
//        return null;
//    }

    private void uploadImages(View view){
        if (imagesList.size() != 0){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Ýüklenýär 0/"+imagesList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < imagesList.size(); i++) {
                final int finalI = i;

                storageReference.child("userData/").child(currentUid).child(imagesList.get(i).getImageName()).putFile(imagesList.get(i).getImageUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageReference.child("userData/").child(currentUid).child(imagesList.get(finalI).getImageName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    counter++;
                                    progressDialog.setMessage("Ýüklendi "+counter+"/"+imagesList.size());
                                    if (task.isSuccessful()){
                                        savedImagesUri.add(task.getResult().toString());
                                    }else{
                                        storageReference.child("userData/").child(currentUid).child(imagesList.get(finalI).getImageName()).delete();
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
        String productId = reference.getId();

        String lowerName = name.toLowerCase(Locale.ROOT);
        List<String> search_key = Arrays.asList(lowerName.split("\\s+"));

        StringBuilder appendableString = new StringBuilder();
        List<String> final_search_key = new ArrayList<>();

        for (String word: search_key) {
            for (int i = 0; i < word.length(); i++) {
                appendableString.append(word.charAt(i));
                final_search_key.add(appendableString.toString());
            }
            appendableString.setLength(0);
        }

        dataMap = new HashMap<>();
        //Below line of code will put your images list as an array in firestore
        dataMap.put("images", savedImagesUri);
        dataMap.put("name", name);
        dataMap.put("location", location);
        dataMap.put("type", type);
        dataMap.put("info", explanation);
        dataMap.put("price", Float.parseFloat(price));
        dataMap.put("category", categoryName);
        dataMap.put("sub_category", subCategoryName);
        dataMap.put("owner_uid", currentUid);
        dataMap.put("phone_number", phoneNumber);
        dataMap.put("date_created", Timestamp.now());
        dataMap.put("product_id", productId);
        dataMap.put("accepted", false);
        dataMap.put("viewed", 0);
        dataMap.put("admin", false);
        dataMap.put("currency", currency);
        dataMap.put("search_key", final_search_key);
        //Below commented code is no more recommended..!
        /*for (int i = 0; i < savedImagesUri.size(); i++) {
            dataMap.put("image" + i, savedImagesUri.get(i));
        }*/
        reference.set(dataMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference = firestore.collection("all_products").document(productId);

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
                });
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