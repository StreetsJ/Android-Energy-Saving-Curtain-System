package tamu.edu.smartcurtain;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAutomatic extends Fragment implements View.OnClickListener {
    private Button go_btn;
    private EditText desired_temp;
    private int open_color;
    BluetoothSocket btSocket = null;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_automatic, container, false);

        // TODO: Get button by ID and set button color to light green
        // Get button object from fragment_automatic.xml
        go_btn = (Button) view.findViewById(R.id.go_btn);

        // Get editText object from xml
        desired_temp = (EditText) view.findViewById(R.id.editTextDesiredTemp);

        // Get color from xml
        open_color = getResources().getColor(R.color.light_green);

        // Set button color to light_green by default;
        go_btn.setBackgroundColor(open_color);

        // Call onClick override for go_btn
        InitView();

        // Get BT Socket for comms
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        btSocket = main.getBtSocket();

        return view;
    }

    private void InitView() {
        go_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO: Override OnClick for "go btn" to display text to user when pressed
        switch (view.getId()) {
            case R.id.go_btn:
                String toastText = "Desired temperature is " + desired_temp.getText().toString();
                Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
