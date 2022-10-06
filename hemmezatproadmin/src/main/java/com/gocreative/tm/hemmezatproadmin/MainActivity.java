package com.gocreative.tm.hemmezatproadmin;
// Coded by: Oraz Gulchayev

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gocreative.tm.hemmezatproadmin.NavigationFragments.AddAdvertiseFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.AddProductFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.CategoriesFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.HomeFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.SearchFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.SettingsFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.WaitinProductsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    Fragment fragment;
    TextView toolbarText;
    DocumentReference documentReference;
    FirebaseAuth user;
    FirebaseFirestore firestore;
    String currentUid, phoneNumber, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        toolbarText = findViewById(R.id.toolbar_text);

        user = FirebaseAuth.getInstance();
        currentUid = user.getUid();
        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_variant);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        View view = navigationView.getHeaderView(0);
        TextView gmailView = view.findViewById(R.id.name_nav);
        TextView phoneNumView = view.findViewById(R.id.phone_number);

        documentReference = firestore.collection("users").document(currentUid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    phoneNumber = task.getResult().getString("phone_number");
                    name = task.getResult().getString("name");
                    gmailView.setText(name);
                    phoneNumView.setText(phoneNumber);

                }else{
                    Log.d("Profile", "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_home:
                toolbarText.setText("Hemmezat");
                fragment = new HomeFragment();
                break;
            case R.id.nav_search:
                toolbarText.setText("Gözleg");
                fragment = new SearchFragment();
                break;
            case R.id.nav_categories:
                toolbarText.setText("Bölümler");
                fragment = new CategoriesFragment();
                break;
            case R.id.nav_add_product:
                Bundle bundle = new Bundle();
                String categoryName = "null";
                String subCategoryName = "null";
                bundle.putString("subCategoryName", categoryName);
                bundle.putString("categoryName", subCategoryName);
                toolbarText.setText("Hemmezat");
                fragment = new AddProductFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_advertise:
                toolbarText.setText("Hemmezat");
                fragment = new AddAdvertiseFragment();
                break;
            case R.id.nav_settings:
                Bundle bundleSettings = new Bundle();
                if (name != null && phoneNumber != null){
                    bundleSettings.putString("name", name);
                    bundleSettings.putString("phone", phoneNumber);
                }else{
                    bundleSettings.putString("name", "null");
                    bundleSettings.putString("phone", "null");
                }
                toolbarText.setText("Sazlamalar");
                fragment = new SettingsFragment();
                fragment.setArguments(bundleSettings);
                break;
            case R.id.nav_logout:
                showLogoutDialog();
                break;
            case R.id.nav_categories_waiting:
                toolbarText.setText("Garaşýan bildirişler");
                fragment = new WaitinProductsFragment();
                break;


        }
        if (fragment != null){
            openFragment(fragment);
        }else{
            Log.d("Fragment Changing", "onNavigationItemSelected: " + "Error in creating fragment!");
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void openFragment(final Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void showLogoutDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_dialog);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button yes = dialog.findViewById(R.id.dialog_yes);
        Button no = dialog.findViewById(R.id.dialog_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.signOut();
                // Delete android app data
                ((ActivityManager) MainActivity.this
                        .getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}