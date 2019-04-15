package e.user.afinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView about1,aboutinfo1,aboutinfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about1 = (TextView) findViewById(R.id.about_app);
        aboutinfo1 = (TextView) findViewById(R.id.about_info1);
        aboutinfo2 = (TextView) findViewById(R.id.about_info2);
    }
}
