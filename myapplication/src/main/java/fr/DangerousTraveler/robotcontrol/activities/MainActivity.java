package fr.DangerousTraveler.robotcontrol.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.UUID;

import fr.DangerousTraveler.robotcontrol.ControlFragment;
import fr.DangerousTraveler.robotcontrol.utils.BluetoothUtils;
import fr.DangerousTraveler.robotcontrol.utils.FilesUtils;
import fr.DangerousTraveler.robotcontrol.R;
import fr.DangerousTraveler.robotcontrol.utils.StabilisationUtils;

public class MainActivity extends AppCompatActivity {

    // adresse MAC de l'appareil auquel se connecter
    private String address;

    private BottomAppBar appBar;

    private LinearLayoutCompat backdrop;

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

    public static boolean joystickControlModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instancier les sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        fabConnectBt = findViewById(R.id.fab_connect_bt);
        backdrop = findViewById(R.id.backdrop);

        // si le stockage externe est accessible, vérifier que l'accès à ce stockage est accordé
        if (FilesUtils.isExternalStorageReadable()) {

            externalStrorageReadAccess();
        }

        // afficher le menu sur l'appBar
        appBar = findViewById(R.id.bottomAppBar);
        appBar.inflateMenu(R.menu.main_menu);

        // gérer le click sur les éléments du menu
        appBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            // gérer le click sur les éléments du menu
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    // accès à l'écran de paramètres
                    case R.id.settings:

                        // démarrer la settingsActivity
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;

                    // changer de thème
                    case R.id.change_theme:

                        // vérifier si le thème foncé est déjà activé
                        if (sharedPreferences.getBoolean("dark_theme_key", false)) {

                            // désactiver le thème foncé
                            sharedPreferences.edit().putBoolean("dark_theme_key", false).apply();

                        } else {

                            // activer le thème foncé
                            sharedPreferences.edit().putBoolean("dark_theme_key", true).apply();
                        }

                        // redémarrer l'activité
                        recreate();
                        return true;

                    default: return false;
                }
            }
        });

        // choisir le thème à afficher
        setTheme();
    }

    // méthode permettant de choisir le thème à utiliser
    private void setTheme() {

        // vérifier si le thème foncé est activé dans les paramètres
        if (sharedPreferences.getBoolean("dark_theme_key", false)) {

            // activer le thème foncé
            setTheme(R.style.AppThemeDark);

            // mettre le backdrop en foncé
            backdrop.setBackgroundResource(R.color.colorDarkBackdrop);

            // afficher la shape arrondie si l'API est de 23 ou +
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                findViewById(R.id.fragment).setBackground(getApplicationContext().getDrawable(R.drawable.backdrop_shape_dark));
            }

            // couleur foncée pour l'appBar
            appBar.setBackgroundResource(R.color.colorPrimaryDark_DarkMode);

            // mettre l'overflow menu en blanc pour Lollipop et ultérieur
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                appBar.setOverflowIcon(getDrawable(R.drawable.ic_overflow_menu));

            // mettre l'iône de navigation en blanc
            appBar.setNavigationIcon(R.drawable.ic_camera_white);

            // gérer l'ouverture et la fermeture du backdrop
            appBar.setNavigationOnClickListener(new NavigationIconClickListener(
                    this,
                    findViewById(R.id.fragment),
                    new AccelerateDecelerateInterpolator(),
                    this.getResources().getDrawable(R.drawable.ic_camera_white),   // icône d'ouverture du backdrop en blanc
                    this.getResources().getDrawable(R.drawable.ic_close_white)));  // icône de fermeture du backdrop en blanc

        } else {

            // mettre le backdrop en clair
            backdrop.setBackgroundResource(R.color.colorBackdrop);

            // afficher la shape arrondie si l'API est de 23 ou +
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                findViewById(R.id.fragment).setBackground(getApplicationContext().getDrawable(R.drawable.backdrop_shape));
            }

            // couleur claire pour l'appBar
            appBar.setBackgroundResource(R.color.colorPrimary);

            // gérer l'ouverture et la fermeture du backdrop
            appBar.setNavigationOnClickListener(new NavigationIconClickListener(
                    this,
                    findViewById(R.id.fragment),
                    new AccelerateDecelerateInterpolator(),
                    this.getResources().getDrawable(R.drawable.ic_camera),   // icône d'ouverture du backdrop
                    this.getResources().getDrawable(R.drawable.ic_close)));  // icône de fermeture du backdrop

        }
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

            // désactiver le joystick
            ControlFragment.joystickView.setEnabled(false);
            ControlFragment.joystickView.setBackgroundColor(getResources().getColor(R.color.colorJoystickBackgroundDisabled));
            ControlFragment.joystickView.setButtonColor(R.color.colorJoystickButtonDisabled);
            joystickControlModeEnabled = false;
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

            // effectuer l'opération si l'appareil n'est pas déjà connecté
            if (bluetoothSocket == null || !bluetoothConnected) {

                // essayer d'établir la connexion avec l'appareil distant
                try {

                    // se connecter à l'appareil grâce à son adresse MAC
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                    //créer une connexion RFCOMM(SPP)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(btUUID);

                    // démarrer la connexion
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();

                    bluetoothConnected = true;

                } catch (IOException ex) {

                    connectSuccess = false;
                }
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

                    // activer le joystick
                    ControlFragment.joystickView.setEnabled(true);
                    ControlFragment.joystickView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ControlFragment.joystickView.setButtonColor(R.color.colorAccent);
                    joystickControlModeEnabled = true;

                    // initialiser les entrées analogiques du robot afin de récupérer les valeurs des axes de l'accéléromètre par bluetooth
                    BluetoothUtils.initialiseAnalogInputs();

                    // stabiliser le robot
                    new StabilisationUtils.StabilisationTask().execute();
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
}
