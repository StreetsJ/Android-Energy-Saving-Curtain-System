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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class FragmentManual extends Fragment implements View.OnClickListener {
    private static Button open_btn, close_btn;
    private static int prim_color, open_color;
    private static boolean botState, userStatus = false;

    View view;

    RequestQueue requestQueue;
    String switchBotCmdUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/commands";
    static String switchBotStatusUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/status";
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

        return view;
    }

    private void InitView() {
        // Override the onclick listener for our own functions below
        open_btn.setOnClickListener(this);
        close_btn.setOnClickListener(this);
    }

    public static void getBotStatus() {
        new BotStatusJsonTask().execute(switchBotStatusUrl);
    }

    public static class BotStatusJsonTask extends AsyncTask<String, String, String> {

        private int status;

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String switchBotKey = "01a025832040cb69271e51c46a1eb29a978c00a605a759041150b0eb7a7f571957245b96c5bc76d268e1958e7ea063f7";

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", switchBotKey);
                connection.setRequestMethod("GET");

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response", "> " + line);   //here u ll get whole response...... :-)
                    JSONObject response = null;
                    try {
                        response = new JSONObject(line);
                        status = Integer.parseInt(response.getJSONObject("body").getString("slidePosition"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Status", " > " + status);
                    botState = status >= 50;
                    if (botState) { // closed
                        open_btn.setBackgroundColor(prim_color);
                        close_btn.setBackgroundColor(open_color);
                    } else { // open
                        open_btn.setBackgroundColor(open_color);
                        close_btn.setBackgroundColor(prim_color);
                    }
                }

                return buffer.toString();


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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_btn:
                open_btn.setBackgroundColor(open_color);
                close_btn.setBackgroundColor(prim_color);
                userStatus = true;
                new JsonTask().execute(switchBotCmdUrl, "open");
                break;
            case R.id.close_btn:
                open_btn.setBackgroundColor(prim_color);
                close_btn.setBackgroundColor(open_color);
                userStatus = false;
                new JsonTask().execute(switchBotCmdUrl, "close");
                break;
            default:
                break;
        }
    }

    public class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage(userStatus ? "Opening curtain..." : "Closing curtain...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String switchBotKey = "01a025832040cb69271e51c46a1eb29a978c00a605a759041150b0eb7a7f571957245b96c5bc76d268e1958e7ea063f7";

            String jsonInput = null;

            if (Objects.equals(params[1], "open")) jsonInput = "{\"command\" : \"setPosition\", \"parameter\" : \"(0, ff, 0)\"}";
            else if (Objects.equals(params[1], "close")) jsonInput = "{\"command\" : \"setPosition\", \"parameter\" : \"(0, ff, 100)\"}";
            else System.out.println("Error with json input string");

            if (jsonInput != null) {
                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", switchBotKey);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

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
            } else {
                System.out.println("Json input is empty");
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

}
