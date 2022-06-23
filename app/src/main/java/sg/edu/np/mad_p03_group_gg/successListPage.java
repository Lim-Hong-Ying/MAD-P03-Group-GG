package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;

public class successListPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_list_page);

        Button backToHome = findViewById(R.id.back_to_home);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnhome = new Intent(view.getContext(), MainActivity.class);
                finish();
                view.getContext().startActivity(returnhome);
            }
        });
    }
}