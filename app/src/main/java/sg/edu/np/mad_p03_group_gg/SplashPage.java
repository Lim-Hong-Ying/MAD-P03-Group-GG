package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //animation
        Animation Animationtop,bottomAniamtion;
        Animationtop= AnimationUtils.loadAnimation(this,R.anim.top_animation_splashscreen);
        bottomAniamtion=AnimationUtils.loadAnimation(this,R.anim.bottom_animation_splashscreen);
        //finding views for aniamtion
        ImageView image =findViewById(R.id.splash_screen_img);
        TextView slogan = findViewById(R.id.slogan);
        TextView name = findViewById(R.id.appname);
        image.setAnimation(Animationtop);
        slogan.setAnimation(bottomAniamtion);
        name.setAnimation(Animationtop);

//        FirebaseApp.initializeApp(/*context=*/ this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());
//
//        //FOR DEBUG USAGE: TO ALLOW APP CHECK
//        firebaseAppCheck.installAppCheckProviderFactory(
//                DebugAppCheckProviderFactory.getInstance());


        FirebaseTools.downloadFiles("advertisement", getApplicationContext(), this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent i = new Intent(SplashPage.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Intent intent = new Intent(SplashPage.this, loginpage.class);
                    startActivity(intent);
                    finish();//prevents user from going back to splashpage
                }

            }
        },5000);//5 seconds
    }
}