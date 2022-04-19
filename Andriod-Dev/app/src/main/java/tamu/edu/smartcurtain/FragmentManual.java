package tamu.edu.smartcurtain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.util.UUID;

public class FragmentManual extends Fragment implements View.OnClickListener {
    private Button open_btn, close_btn;
    private int prim_color, open_color;

    BluetoothSocket btSocket = null;

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

        // Get BT Socket for comms
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        btSocket = main.getBtSocket();

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
                sendSignal("2");
                break;
            case R.id.close_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(open_color);
                sendSignal("1");
                break;
            default:
                break;
        }
    }

    private void sendSignal(String num) {
        if (btSocket != null) {
            try {
                System.out.println("Attempting send of " + num);
                btSocket.getOutputStream().write(num.toString().getBytes());
            } catch (IOException e) {
                msg("Error sending msg");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    public void recvBTConn(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }
}
