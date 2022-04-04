package tamu.edu.smartcurtain;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button open_btn, close_btn, man_btn, auto_btn;
    private int prim_color, open_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign xml buttons to java btn objects to extract/manipulate data
        open_btn = (Button) findViewById(R.id.open_btn);
        close_btn = (Button) findViewById(R.id.close_btn);
        man_btn = (Button) findViewById(R.id.man_btn);
        auto_btn = (Button) findViewById(R.id.auto_btn);

        // Override the onclick listener for our own functions below
        open_btn.setOnClickListener(this);
        close_btn.setOnClickListener(this);
        man_btn.setOnClickListener(this);
        auto_btn.setOnClickListener(this);

        // get colors from xml
        prim_color = getResources().getColor(R.color.light_blue);
        open_color = getResources().getColor(R.color.light_green);

        man_btn.setBackgroundColor(open_color);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_btn:
                open_btn.setBackgroundColor(open_color);
                close_btn.setBackgroundColor(prim_color);
                break;
            case R.id.close_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(open_color);
                break;
            case R.id.man_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(prim_color);

                man_btn.setBackgroundColor(Color.MAGENTA);
                auto_btn.setBackgroundColor(prim_color);

                open_btn.setEnabled(true);
                close_btn.setEnabled(true);
                break;
            case R.id.auto_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(prim_color);

                auto_btn.setBackgroundColor(Color.MAGENTA);
                man_btn.setBackgroundColor(prim_color);

                open_btn.setEnabled(false);
                close_btn.setEnabled(false);
                break;
            default:
                break;
        }
    }
}