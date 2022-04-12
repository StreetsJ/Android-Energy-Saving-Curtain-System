package tamu.edu.smartcurtain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import tamu.edu.smartcurtain.databinding.ActivityMainBinding;
import tamu.edu.smartcurtain.ui.main.SectionsPagerAdapter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton settingsBtn;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        settingsBtn = (ImageButton) findViewById(R.id.imageButton);
        settingsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                Intent settingsAct = new Intent(this, SettingsActivity.class);
                startActivity(settingsAct);
                break;
            default:
                break;
        }
    }
}