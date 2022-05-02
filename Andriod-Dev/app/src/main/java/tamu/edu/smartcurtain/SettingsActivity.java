package tamu.edu.smartcurtain;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    RequestQueue requestQueue;
    String switchBotCmdUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/status";
    ProgressDialog pd;
    BluetoothSocket btSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnConnect = (Button) findViewById(R.id.connectBtn);
        Button getJSON = (Button) findViewById(R.id.botStatusButton);
        Button testArduino = (Button) findViewById(R.id.testArduinoButton);

        getJSON.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        testArduino.setOnClickListener(this);

        btSocket = FragmentAutomatic.getSocket();

        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botStatusButton:
                new JsonTask().execute(switchBotCmdUrl);
                break;
            case R.id.connectBtn:
                Disconnect();
                break;
            case R.id.testArduinoButton:
                sendSignal("*");
                break;
            default:
                break;
        }
    }

    private void sendSignal(String data) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                msg("Error sending " + data + " to arduino");
            }
        }
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(SettingsActivity.this);
            pd.setMessage("Please wait");
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
                connection.setRequestMethod("GET");

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
//                    msg(line);
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

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}