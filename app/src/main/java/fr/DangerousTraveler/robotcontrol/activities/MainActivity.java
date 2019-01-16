package fr.DangerousTraveler.robotcontrol.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import fr.DangerousTraveler.robotcontrol.BluetoothUtils;
import fr.DangerousTraveler.robotcontrol.ControlFragment;
import fr.DangerousTraveler.robotcontrol.FilesUtils;
import fr.DangerousTraveler.robotcontrol.R;

public class MainActivity extends AppCompatActivity {

    // texte de la reconnaissance vocale
    public static String speechText;

    // adresse MAC de l'appareil auquel se connecter
    private String address;

    private BottomAppBar appBar;

    // état de la connexion bluetooth
    public static boolean bluetoothConnected = false;

    // requête d'activation du bluetooth
    private final static int REQUEST_ENABLE_BT = 1;

    // requête de conexion bluetooth à un appareil appairé
    private final static int REQUEST_CONNECT_PAIRED_DEVICE = 2;

    // reqûete pour donner la permission d'accéder au stockage externe
    private final static int REQUEST_READ_EXTERNAL_STORAGE = 3;

    // adapter permettant d'utiliser les fonctionnalités bluetooth
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothSocket bluetoothSocket = null;

    public static SharedPreferences sharedPreferences;

    // UUID utilisé pour la connexion bluetooth
    private static final UUID btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private FloatingActionButton fabConnectBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instancier les sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        // si le stockage externe est accessible, vérifier que l'accès à ce stockage est accordé
        if (FilesUtils.isExternalStorageReadable()) {

            externalStrorageReadAccess();
        }

        // afficher le menu sur la bottomAppBar
        appBar = findViewById(R.id.bottomAppBar);
        appBar.inflateMenu(R.menu.main_menu);
        appBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            // gérer le click sur les éléments du menu
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.settings:

                        // démarrer la settingsActivity
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.speech:

                        if (bluetoothConnected) {

                            // démarrer la reconnaissance vocale
                            startSpeechRecognisation();
                        }
                        return true;

                    default: return false;
                }
            }
        });

        fabConnectBt = findViewById(R.id.fab_connect_bt);
    }

    // méthode permettant d'activer le bluetooth s'il est désactivé
    public void isBluetoothEnabled() {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // méthode permettant de gérer les click sur le bouton de connexion/ déconnection du bluetooth
    public void onClicConnectBtn(View view) {

        // si l'appareil n'est pas encore connecté, essayer d'établir la connexion
        if (!bluetoothConnected) {

            // vérifier la disponibilité du bluetooth
            if (BluetoothUtils.isBluetoothAvailable()) {

                // activer le bluetooth s'il est désactivé
                isBluetoothEnabled();

                // si le bluetooth est activé, démarrer la BluetoothDevicesActivity pour choisir un appareil auquel se connecter
                if (mBluetoothAdapter.isEnabled()) {

                    Intent activityIntent = new Intent(MainActivity.this, BluetoothDevicesActivity.class);
                    startActivityForResult(activityIntent, REQUEST_CONNECT_PAIRED_DEVICE);
                }
            }

            // si l'appareil est déjà connecté, procéder à la déconnexion
        } else {

            // si le socket n'est pas vide, essayer de le fermer
            if (bluetoothSocket != null) {

                try {

                    bluetoothSocket.close();
                } catch (IOException ex) {

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }

            Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
            bluetoothConnected = false;

            fabConnectBt.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            fabConnectBt.setImageResource(R.drawable.ic_bt_disabled);
        }
    }

    // gérer les informations reçues depuis la BluetoothDevicesActivity
    @Override
    protected void onActivityResult(int RequestCode, int ResultCode, Intent data) {

        // afficher un message d'erreur si le bluetooth n'a pas été activé
        if (RequestCode == REQUEST_ENABLE_BT) {

            if (ResultCode != RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth non activé", Toast.LENGTH_SHORT).show();
            }
        }

        if (RequestCode == REQUEST_CONNECT_PAIRED_DEVICE) {

            if (ResultCode == RESULT_OK) {

                // si un appareil appairé a été sélectionné, récupérer son adresse MAC
                address = data.getStringExtra("EXTRA_ADDRESS");

                // établir la connexion bluetooth
                new connectBluetooth().execute();
            }
        }

        // reconnaissance vocale
        if (RequestCode == 8) {
            if (ResultCode == RESULT_OK && null != data) {

                // récupérer le texte de la reconnaissance vocale
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                speechText = result.get(0);

                // démarrer l'action souhaitée
                ControlFragment.startActionAfterSpeech();
            }
        }
    }

    // inner class permettant d'effectuer la connexion bluetooth en arrière plan
    private class connectBluetooth extends AsyncTask<Void, Void, Void> {

        boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {

            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // essayer d'établir la connexion avec l'appareil distant
            try {

                // effectuer l'opération si l'appareil n'est pas déjà connecté
                if (bluetoothSocket == null || bluetoothConnected) {

                    // se connecter à l'appareil grâce à son adresse MAC
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                    //créer une connexion RFCOMM(SPP)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(btUUID);

                    // démarrer la connexion
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            } catch (IOException ex) {

                connectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // si la connexion aéchouée, afficher un message d'erreur
            if (!connectSuccess) {

                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_LONG).show();
            }

            // sinon informer l'utilisateur que la connexion s'est effectuée avec succès
            else {

                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                bluetoothConnected = true;

                fabConnectBt.setBackgroundTintList(getResources().getColorStateList(R.color.colorBtConnected));
                fabConnectBt.setImageResource(R.drawable.ic_bt_connected);

                // mettre les positions d'étalonnage des servoMoteurs dans une String
                String calibrationServoPos = BluetoothUtils.initServoPos();

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);
            }
        }

    }

    // vérifier que l'on a accès aux fichiers externes en écriture
    private void externalStrorageReadAccess() {

        // vérifier que la version d'android est au moins Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // vérifier si la permission n'est pas déjà accordée
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);

            }
        }
    }

    //démarrer la reconnaissance vocale
    public void startSpeechRecognisation() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // langue de la reconnaissance vocale
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, 8);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
