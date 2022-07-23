package sg.edu.np.mad_p03_group_gg.view.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.view.ui.fragments.User_Profile_Fragment;
import sg.edu.np.mad_p03_group_gg.databinding.ActivityMainBinding;
import sg.edu.np.mad_p03_group_gg.newlisting;
import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // When MainActivity is launched, it will display homepage as default
        replaceFragment(new HomepageFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Get which button the user pressed and display the fragment accordingly
            switch(item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomepageFragment());
                    break;
                case R.id.add:
                    // Initiate the function of adding new post/listing
                    Intent newList = new Intent(this, newlisting.class);
                    this.startActivity(newList);
                    break;
                case R.id.user:
                    // Replace the fragment with User Profile Fragment
                    replaceFragment(new User_Profile_Fragment());
                    break;
            }

            return true;
        });

    }

    // Init the fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Find the frame layout for replacing fragments
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // Apply changes
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setTitle("Exiting App")
                .setMessage("Do you want to exit Cashshope?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}