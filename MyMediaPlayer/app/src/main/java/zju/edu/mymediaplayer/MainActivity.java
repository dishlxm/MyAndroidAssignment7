package zju.edu.mymediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button imageButton;
    Button videoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton = (Button) findViewById(R.id.image_button);
        videoButton = (Button) findViewById(R.id.video_button);
        imageButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.image_button:
                startActivity(new Intent(MainActivity.this, GlideActivity.class));
                break;
            case R.id.video_button:
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;
            default:
                break;
        }
    }
}
