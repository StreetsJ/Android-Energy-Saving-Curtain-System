package tamu.edu.smartcurtain;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import java.util.Objects;
import java.util.UUID;

public class FragmentAutomatic extends Fragment implements View.OnClickListener {
    private Button go_btn;
    private static EditText desired_temp;
    private static EditText current_temp;
    private int open_color;
    private static float actual_temp, curr_temp, des_temp = 0.0F;
    static ProgressDialog pd;
    String address = "00:21:07:34:D7:38";
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static ConnectedThread conThread;
    static AlgorithmLogic algo;
    static boolean isFloat, botState = false;
    String switchBotCmdUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/commands";
    static String switchBotStatusUrl = "https://api.switch-bot.com/v1.0/devices/CCDB63AB30A7/status";

    View view;

    public static BluetoothSocket getSocket() {
        return btSocket;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_automatic, container, false);

        // Get button object from fragment_automatic.xml
        go_btn = view.findViewById(R.id.go_btn);

        // Get editText object from xml
        desired_temp = view.findViewById(R.id.editTextDesiredTemp);
        current_temp = view.findViewById(R.id.editTextCurrentTemp);

        // Get color from xml
        open_color = getResources().getColor(R.color.light_green);

        // Set button color to light_green by default;
        go_btn.setBackgroundColor(open_color);

        // Call onClick override for go_btn
        InitView();

        // Get BT Socket for comms
        new ConnectBT().execute();

        algo = new AlgorithmLogic(0.0F, 0.0F, 0.0F);

        return view;
    }

    private void InitView() {
        go_btn.setOnClickListener(this);
    }

    private void sendSignal(String data) {
        if (btSocket != null) {
            try {
                msg("Attempting to send data to arduino");
                btSocket.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                msg("Error sending " + data + " to arduino");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_btn:
                try {
                    curr_temp = Float.parseFloat(current_temp.getText().toString());
                    des_temp = Float.parseFloat(desired_temp.getText().toString());
                } finally {
                    isFloat = true;
                    algo.setCurr_temp(curr_temp);
                    algo.setDesired_temp(des_temp);
                }
//                String toastText = "Desired temperature is " + desired_temp.getText().toString();
//                msg(toastText);
//                msg("Current temp is " + current_temp.getText().toString());
                if (!conThread.isAlive()) {
                    conThread.start();
                }
                conThread.write("*".getBytes(StandardCharsets.UTF_8));
                break;
            default:
                break;
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getContext(), "Connecting...", "Please Wait!!!");
        }

        @RequiresApi(api = Build.VERSION_CODES.S)
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
//                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
                conThread = new ConnectedThread(btSocket);
            }

            progress.dismiss();
        }
    }

    private void msg(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = msg.arg1;
            int end = msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    if (Float.parseFloat(writeMessage) > 120.0 || Float.parseFloat(writeMessage) < 20.0)
                    {
                        System.out.println("Error in reading: " + writeMessage);
                        conThread.write("*".getBytes(StandardCharsets.UTF_8));
                    }
                    else
                    {
                        actual_temp = Float.parseFloat(writeMessage);
                        System.out.println("Reading is " + actual_temp);
                        if (algo != null && isFloat) {
                            new BotStatusJsonTask().execute(switchBotStatusUrl);
                            algo.computeShouldOpen();

                            if (algo.getWindow_temp() != actual_temp) {
                                algo.setWindow_temp(actual_temp);
                                // TODO: Compute algorithm

                                if (botState != algo.isShouldOpen()) new JsonTask().execute(switchBotCmdUrl, algo.isShouldOpen() ? "open" : "close");
                                System.out.println("Window should be " + (algo.isShouldOpen() ? "open" : "closed"));
                            }
                            if (botState != algo.isShouldOpen()) new JsonTask().execute(switchBotCmdUrl, algo.isShouldOpen() ? "open" : "close");

                            else System.out.println("No change in temp");
                        }
                        // TODO: Call the function/thread to request api for open/close
//                        new JsonTask().execute(switchBotCmdUrl, algo.isShouldOpen() ? "open" : "close");
                    }
                    break;
            }
        }
    };

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage(algo.isShouldOpen() ? "Opening curtain..." : "Closing curtain...");
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
            System.out.println("Reached post execute");
            if (pd.isShowing()){
                pd.dismiss();
            }
        }
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
                    botState = status <= 50;
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
}
