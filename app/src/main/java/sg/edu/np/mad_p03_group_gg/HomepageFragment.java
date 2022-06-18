package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.models.AdBannerImage;
import sg.edu.np.mad_p03_group_gg.view.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        // Download images from /advertisement which are stored as temp files
        File dir = new File(getContext().getCacheDir(),"advertisement");
        ArrayList<String> filePaths = new ArrayList<>();

        if (dir.exists()) {
            if (dir.listFiles().length == 0) {
                downloadFiles("advertisement");
            }
            for (File f : dir.listFiles()) {
                filePaths.add(f.getAbsolutePath());
            }
        }
        else {
            downloadFiles("advertisement");
        }

        ArrayList<AdBannerImage> adBannerImages = new ArrayList<>();

        for (int i = 0; i < filePaths.size(); i++) {
            AdBannerImage adBannerImage = new AdBannerImage(filePaths.get(i));
            adBannerImages.add(adBannerImage);
        }

        // Implementation of a Horizontal ViewPager2, able to scroll and snap
        // Refer to https://www.youtube.com/watch?v=O8LA26sAt7Y
        ViewPager2 viewPager2 = view.findViewById(R.id.AdBannerView);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(adBannerImages);

        viewPager2.setAdapter(viewPagerAdapter);

        // Set onClickListeners for Buttons
        CardView listingsCardView = view.findViewById(R.id.listingsButton);
        CardView meetingPlannerCardView = view.findViewById(R.id.meetingPlannerButton);
        ImageView chatButtonView = view.findViewById(R.id.chatButton);

        listingsCardView.setOnClickListener(v -> {
            // When clicked, will bring to listings page which displays all listings
            replaceFragment(new listingFragment());
        });

        chatButtonView.setOnClickListener(v -> {
            Intent chatListIntent = new Intent(getContext(), ChatList.class);
            startActivity(chatListIntent);
        });

        meetingPlannerCardView.setOnClickListener(v -> {
            // When clicked, will bring to meeting planner page which displays all listings
            Intent meetingPlannerIntent = new Intent(getContext(), this.getClass());
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void downloadFiles(String folder) {
        // Init Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cashoppe-179d4.appspot.com/");

        // Create a storage reference, basically a pointer to a file in the Firebase cloud
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference imagesRef = storageRef.child(folder);

        // List all images in /<folder> eg. can be /advertisement
        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference fileRef : listResult.getItems()) {
                            // TODO: Download the file using its reference (fileRef)

                            // Download files from the folder, eg. images from /advertisement
                            try {
                                File outputDirectory = new File(getContext().getCacheDir(), folder);
                                if (!outputDirectory.exists()) {
                                    outputDirectory.mkdirs();
                                }

                                File localFile = File.createTempFile("shopee", ".jpg", outputDirectory);
                                fileRef.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                            catch (Exception e) {
                                Log.e("Unable to download image", String.valueOf(e));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        Toast.makeText(getActivity(),
                                "An Error Occured: Unable to List Items from Firebase",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean searchCache(ArrayList<String> fileNames, String directory) {
        File storagePath = new File(Environment.getExternalStorageDirectory(), directory);
        return true;
    }
}
