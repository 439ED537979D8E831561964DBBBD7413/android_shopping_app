package course.android.shopping_example_app.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import shopping_example_app.R;
import course.android.shopping_example_app.Utills.Constants;

public class WelcomeActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    }

    public void LaunchAmjad(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "aNas");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }

    public void LaunchAiman(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "aYoun");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();

    }

    public void LaunchJohn(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "jSmith");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }
}
