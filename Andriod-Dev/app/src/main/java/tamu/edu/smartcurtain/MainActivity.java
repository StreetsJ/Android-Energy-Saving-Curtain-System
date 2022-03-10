package tamu.edu.smartcurtain;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Button open_btn, close_btn, man_btn, auto_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign xml buttons to java btn objects to extract/manipulate data
        open_btn = (Button) findViewById(R.id.open_btn);
        close_btn = (Button) findViewById(R.id.close_btn);
        man_btn = (Button) findViewById(R.id.man_btn);
        auto_btn = (Button) findViewById(R.id.auto_btn);
    }

    public void onOpenClick(View view) {
        open_btn.setBackgroundColor(Color.GREEN);
        close_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
    }

    public void onCloseClick(View view) {
        open_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
        close_btn.setBackgroundColor(Color.GREEN);
    }

    public void onManualClick(View view) {
        open_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
        close_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));

        man_btn.setBackgroundColor(Color.MAGENTA);
        auto_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));

        open_btn.setEnabled(true);
        close_btn.setEnabled(true);
    }

    public void onAutoClick(View view) {
        open_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));
        close_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));

        auto_btn.setBackgroundColor(Color.MAGENTA);
        man_btn.setBackgroundColor(getResources().getColor(R.color.purple_500));

        open_btn.setEnabled(false);
        close_btn.setEnabled(false);
    }
}