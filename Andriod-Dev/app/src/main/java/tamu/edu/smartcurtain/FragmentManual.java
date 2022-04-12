package tamu.edu.smartcurtain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentManual extends Fragment implements View.OnClickListener {
    private Button open_btn, close_btn;
    private int prim_color, open_color;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manual, container, false);

        // Assign xml buttons to java btn objects to extract/manipulate data
        open_btn = (Button) view.findViewById(R.id.open_btn);
        close_btn = (Button) view.findViewById(R.id.close_btn);

        InitView();

        // get colors from xml
        prim_color = getResources().getColor(R.color.light_blue);
        open_color = getResources().getColor(R.color.light_green);

        return view;
    }

    private void InitView() {
        // Override the onclick listener for our own functions below
        open_btn.setOnClickListener(this);
        close_btn.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
