package com.jasbir.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.jasbir.instaclone.Fragment.HomeFragment;
import com.jasbir.instaclone.Fragment.NotificationFragment;
import com.jasbir.instaclone.Fragment.ProfileFragment;
import com.jasbir.instaclone.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch(menuItem.getItemId()){

                        case R.id.home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.add:
                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this,PostActivity.class));
                            break;
                        case R.id.like:
                            selectedFragment = new NotificationFragment();
                            Toast.makeText(MainActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.user:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;


                    }

                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                    }
                    return true;
                }
            };

}
