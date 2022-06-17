package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class newlisting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlisting);

        ImageView selectimage = findViewById(R.id.choose_image);
        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        Button createlisting = findViewById(R.id.create_listing);
        createlisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToDatabaseAndFirebase();

                Intent returnhome = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(returnhome);
            }
        });
    }

    private void writeToDatabaseAndFirebase() {
        String dblink = "gs://cashoppe-179d4.appspot.com";
        StorageReference db = FirebaseStorage.getInstance(dblink).getReference().child("listing-images");

        long currenttime = new Date().getTime();
        final StorageReference newfilename = db.child("" + currenttime); //add userid for further uniqueness
        ImageView selectimage = findViewById(R.id.choose_image);

        selectimage.setDrawingCacheEnabled(true);
        selectimage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectimage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = newfilename.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                createListingObject(imageUrl);
                            }
                        });
                    }
                }
            }
        });
    }
}