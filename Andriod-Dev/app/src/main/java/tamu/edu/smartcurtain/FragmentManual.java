package tamu.edu.smartcurtain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FragmentManual extends Fragment implements View.OnClickListener {
    private Button open_btn, close_btn;
    private int prim_color, open_color;

    BluetoothSocket btSocket = null;

    View view;

    RequestQueue requestQueue;
    String switchBotCmdUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/commands";
    ProgressDialog pd;

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
                new OpenJsonTask().execute(switchBotCmdUrl);
                break;
            case R.id.close_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(open_color);
                new CloseJsonTask().execute(switchBotCmdUrl);
                break;
            default:
                break;
        }
    }

    public class OpenJsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Opening curtain...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String switchBotKey = "01a025832040cb69271e51c46a1eb29a978c00a605a759041150b0eb7a7f571957245b96c5bc76d268e1958e7ea063f7";

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", switchBotKey);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonInput = "{\"command\" : \"setPosition\", \"parameter\" : \"(0, ff, 0)\"}";
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
        }
    }

    public class CloseJsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("CLosing curtain...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String switchBotKey = "01a025832040cb69271e51c46a1eb29a978c00a605a759041150b0eb7a7f571957245b96c5bc76d268e1958e7ea063f7";

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", switchBotKey);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonInput = "{\"command\" : \"setPosition\", \"parameter\" : \"(0, ff, 100)\"}";
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
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
