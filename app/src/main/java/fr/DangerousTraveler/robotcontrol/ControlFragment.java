package fr.DangerousTraveler.robotcontrol;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Objects;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;
import fr.DangerousTraveler.robotcontrol.utils.BluetoothUtils;
import fr.DangerousTraveler.robotcontrol.utils.FilesUtils;

public class ControlFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static ImageButton btn_up, btn_down, btn_left, btn_right, btn_stop;

    private static String command = "stop";

    public static JoystickView joystickView;

    // position des servo moteurs lors de la marche avant
    //étape 1
    private static int SERVO25_POS1_FORWARD = 500;
    private static int SERVO17_POS1_FORWARD = 500;
    private static int SERVO5_POS1_FORWARD = 2500;
    //étape 2
    private static int SERVO24_POS2_FORWARD = 1165;
    private static int SERVO16_POS2_FORWARD = 1110;
    private static int SERVO4_POS2_FORWARD = 1770;
    //étape 3
    private static int SERVO25_POS3_FORWARD = 1300;
    private static int SERVO17_POS3_FORWARD = 1361;
    private static int SERVO5_POS3_FORWARD = 1500;
    //étape 4
    private static int SERVO9_POS4_FORWARD = 2500;
    private static int SERVO21_POS4_FORWARD = 500;
    private static int SERVO1_POS4_FORWARD = 2500;
    //étape 5
    private static int SERVO8_POS5_FORWARD = 1930;
    private static int SERVO20_POS5_FORWARD = 1179;
    private static int SERVO0_POS5_FORWARD = 1830;
    //étape 6
    private static int SERVO9_POS6_FORWARD = 1421;
    private static int SERVO21_POS6_FORWARD = 1566;
    private static int SERVO1_POS6_FORWARD = 1780;

    // position des servo moteurs lors de la marche arrière
    //étape 1
    private static int SERVO1_POS1_BACKWARD = 2500;
    private static int SERVO9_POS1_BACKWARD = 2500;
    private static int SERVO21_POS1_BACKWARD = 500;
    //étape 2
    private static int SERVO0_POS2_BACKWARD = 1080;
    private static int SERVO8_POS2_BACKWARD = 1180;
    private static int SERVO20_POS2_BACKWARD = 1929;
    //étape 3
    private static int SERVO1_POS3_BACKWARD = 1707;
    private static int SERVO9_POS3_BACKWARD = 1421;
    private static int SERVO21_POS3_BACKWARD = 1566;
    //étape 4
    private static int SERVO5_POS4_BACKWARD = 2500;
    private static int SERVO17_POS4_BACKWARD = 500;
    private static int SERVO25_POS4_BACKWARD = 500;
    //étape 5
    private static int SERVO4_POS5_BACKWARD = 1020;
    private static int SERVO16_POS5_BACKWARD = 1860;
    private static int SERVO24_POS5_BACKWARD = 1915;
    //étape 6
    private static int SERVO5_POS6_BACKWARD = 1500;
    private static int SERVO17_POS6_BACKWARD = 1361;
    private static int SERVO25_POS6_BACKWARD = 1300;

    // position des servo moteurs lors de la rotation à gauche
    //étape 1
    private static int SERVO17_POS1_TURN_LEFT = 500;
    private static int SERVO25_POS1_TURN_LEFT = 500;
    private static int SERVO5_POS1_TURN_LEFT = 2500;
    //étape 2
    private static int SERVO16_POS2_TURN_LEFT = 1860;
    private static int SERVO24_POS2_TURN_LEFT = 1915;
    private static int SERVO4_POS2_TURN_LEFT = 1820;
    //étape 3
    private static int SERVO17_POS3_TURN_LEFT = 1361;
    private static int SERVO25_POS3_TURN_LEFT = 1300;
    private static int SERVO5_POS3_TURN_LEFT = 1500;
    //étape 4
    private static int SERVO1_POS4_TURN_LEFT = 2500;
    private static int SERVO9_POS4_TURN_LEFT = 2500;
    private static int SERVO21_POS4_TURN_LEFT = 500;
    //étape 5
    private static int SERVO0_POS5_TURN_LEFT = 1880;
    private static int SERVO8_POS5_TURN_LEFT = 1980;
    private static int SERVO20_POS5_TURN_LEFT = 1929;
    //étape 6
    private static int SERVO1_POS6_TURN_LEFT = 1707;
    private static int SERVO9_POS6_TURN_LEFT = 1421;
    private static int SERVO21_POS6_TURN_LEFT = 1566;

    // position des servo moteurs lors de la rotation à droite
    //étape 1
    private static int SERVO17_POS1_TURN_RIGHT = 500;
    private static int SERVO25_POS1_TURN_RIGHT = 500;
    private static int SERVO5_POS1_TURN_RIGHT = 2500;
    //étape 2
    private static int SERVO16_POS2_TURN_RIGHT = 1060;
    private static int SERVO24_POS2_TURN_RIGHT = 1160;
    private static int SERVO4_POS2_TURN_RIGHT = 1030;
    //étape 3
    private static int SERVO17_POS3_TURN_RIGHT = 1150;
    private static int SERVO25_POS3_TURN_RIGHT = 1100;
    private static int SERVO5_POS3_TURN_RIGHT = 1450;
    //étape 4
    private static int SERVO1_POS4_TURN_RIGHT = 2500;
    private static int SERVO9_POS4_TURN_RIGHT = 2500;
    private static int SERVO21_POS4_TURN_RIGHT = 500;
    //étape 5
    private static int SERVO0_POS5_TURN_RIGHT = 1000;
    private static int SERVO8_POS5_TURN_RIGHT = 1100;
    private static int SERVO20_POS5_TURN_RIGHT = 1050;
    //étape 6
    private static int SERVO1_POS6_TURN_RIGHT = 1400;
    private static int SERVO9_POS6_TURN_RIGHT = 1400;
    private static int SERVO21_POS6_TURN_RIGHT = 1450;


    // String contenant les positions d'étalonnage des servoMoteurs
    private static String calibrationServoPos;

    private static int travellingTime;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_control, container, false);
        btn_up = rootView.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);
        btn_left = rootView.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = rootView.findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        btn_down = rootView.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);
        btn_stop = rootView.findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        joystickView = rootView.findViewById(R.id.joystick);

        // vérifier si l'utilisateur souhaite activer le contrôle avec joystick
        if (MainActivity.sharedPreferences.getBoolean("switch_control_mode", false)) {

            // afficher le joystick et cacher les boutons
            joystickView.setVisibility(View.VISIBLE);
            btn_up.setVisibility(View.INVISIBLE);
            btn_down.setVisibility(View.INVISIBLE);
            btn_left.setVisibility(View.INVISIBLE);
            btn_right.setVisibility(View.INVISIBLE);
            btn_stop.setVisibility(View.INVISIBLE);

            // gérer les différents mouvements du joystick
            joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
                @Override
                public void onMove(int angle, int strength) {

                    // changer la valeure du temps de déplacement en fonction de la force exercée sur le joystick
                    setTravellingTime(strength);

                    // changer le mouvement du mobile en fonction de la direction du joystick
                    setVehicleCommand(angle);

                    // si la force exercée sur le joystick est égale à 0, dire au mobile de s'arrêter
                    if (strength == 0) {

                        // vérifier que l'ordre de s'arrêter n'a pas déjà été donné
                        if (!command.equals("stop"))
                            stop();
                    }
                }
            });
        } else {

            // afficher les boutons et cacher le joystick
            joystickView.setVisibility(View.INVISIBLE);
            btn_up.setVisibility(View.VISIBLE);
            btn_down.setVisibility(View.VISIBLE);
            btn_left.setVisibility(View.VISIBLE);
            btn_right.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.VISIBLE);
        }
        // mettre les positions d'étalonnage des servoMoteurs dans une String
        calibrationServoPos = BluetoothUtils.initServoPos();

        // enregistrer le changement des préférences
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // désenregistrer le changement des préférences
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    // méthode permettant de modifier la vitesse de déplacement du mobile en fonction de la force appliquée au joystick
    private void setTravellingTime(int joyStickStrength) {

        if (joyStickStrength > 95)
            MainActivity.sharedPreferences.edit().putString("settings_travelling_time", "120").apply();

        else if (joyStickStrength > 75)
            MainActivity.sharedPreferences.edit().putString("settings_travelling_time", "250").apply();

        else if (joyStickStrength > 50)
            MainActivity.sharedPreferences.edit().putString("settings_travelling_time", "500").apply();

        else if (joyStickStrength > 25)
            MainActivity.sharedPreferences.edit().putString("settings_travelling_time", "750").apply();

        else if (joyStickStrength > 0)
            MainActivity.sharedPreferences.edit().putString("settings_travelling_time", "1000").apply();

        // récupérer la valeure actuelle du temps de déplacement du mobile
        travellingTime = Integer.parseInt(Objects.requireNonNull(MainActivity.sharedPreferences.getString("settings_travelling_time", "2000")));
    }

    // méthode permettant de récupérer la valeur du temps de déplacement inscrite dans les paramètres
    private void getTravellingTime() {

        travellingTime = Integer.parseInt(Objects.requireNonNull(MainActivity.sharedPreferences.getString("settings_travelling_time", "2000")));
    }

    // méthode permettant de donner un ordre de mouvement au mobile en fonction de la position du joystick
    private void setVehicleCommand(int angle) {

        // dire au mobile d'avancer si ça n'est pas déjà fait
        if (angle >45 && angle<135) {

            if (!command.equals("forward")) {
                new forward().execute();
            }
        } // dire au mobile d'effectuer une rotation à gauche si ça n'est pas déjà fait
        else if (angle >135 && angle<225) {

            if (!command.equals("turnLeft")) {
                new turnLeft().execute();
            }
        } // dire au mobile de reculer si ça n'est pas déjà fait
        else if (angle >225 && angle<315) {

            if (!command.equals("backward")) {
                new backward().execute();
            }
        } // dire au mobile de reculer si ça n'est pas déjà fait
        else if ((angle >315 && angle<365) || (angle >=0 && angle <45)) {

            if (!command.equals("turnRight")) {
                new turnRight().execute();
            }
        }
    }

    // gérer les clicks sur les boutons de contrôle
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            // faire avancer le robot
            case R.id.btn_up:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // récupérer la valeur du temps de déplacement du mobile inscrite dans les paramètres
                    getTravellingTime();

                    // faire avancer le robot
                    new forward().execute();
                }
                break;

            // faire reculer le robot
            case R.id.btn_down:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // récupérer la valeur du temps de déplacement du mobile inscrite dans les paramètres
                    getTravellingTime();

                    // faire reculer le robot
                    new backward().execute();
                }
                break;

            // faire tourner le robot à gauche
            case R.id.btn_left:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // récupérer la valeur du temps de déplacement du mobile inscrite dans les paramètres
                    getTravellingTime();

                    // faire tourner le robot à gauche
                    new turnLeft().execute();
                }
                break;

            // faire tourner le robot à droite
            case R.id.btn_right:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // récupérer la valeur du temps de déplacement du mobile inscrite dans les paramètres
                    getTravellingTime();

                    // faire tourner le robot à droite
                    new turnRight().execute();
                }
                break;

            // arrêter le robot
            case R.id.btn_stop:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // arrêter le robot
                    stop();
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // vérifier le changement du switch du mode de contrôle
        if (key.equals("switch_control_mode")) {

            // vérifier si le mode de contrôle avec joystick est activé
            if (sharedPreferences.getBoolean(key, false)) {

                // afficher le joystick et cacher les boutons
                joystickView.setVisibility(View.VISIBLE);
                btn_up.setVisibility(View.INVISIBLE);
                btn_down.setVisibility(View.INVISIBLE);
                btn_left.setVisibility(View.INVISIBLE);
                btn_right.setVisibility(View.INVISIBLE);
                btn_stop.setVisibility(View.INVISIBLE);
            } else {

                // afficher les boutons et cacher le joystick
                joystickView.setVisibility(View.INVISIBLE);
                btn_up.setVisibility(View.VISIBLE);
                btn_down.setVisibility(View.VISIBLE);
                btn_left.setVisibility(View.VISIBLE);
                btn_right.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.VISIBLE);
            }
        }
    }

    // inner class permettant de faire avancer le robot
    public static class forward extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            command = "forward";

            btn_up.setImageResource(R.drawable.ic_up_enabled);
            btn_left.setImageResource(R.drawable.ic_left);
            btn_right.setImageResource(R.drawable.ic_right);
            btn_down.setImageResource(R.drawable.ic_down);
            btn_stop.setImageResource(R.drawable.ic_stop);

            // stabiliser le mobile
            stabilizeVehicle();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (command.equals("forward")) {
                String data = "#5P" + SERVO5_POS1_FORWARD + "#17P" + SERVO17_POS1_FORWARD + "#25P" + SERVO25_POS1_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#4P" + SERVO4_POS2_FORWARD + "#16P" + SERVO16_POS2_FORWARD + "#24P" + SERVO24_POS2_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#5P" + SERVO5_POS3_FORWARD + "#17P" + SERVO17_POS3_FORWARD + "#25P" + SERVO25_POS3_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS4_FORWARD + "#9P" + SERVO9_POS4_FORWARD + "#21P" + SERVO21_POS4_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#0P" + SERVO0_POS5_FORWARD + "#8P" + SERVO8_POS5_FORWARD + "#20P" + SERVO20_POS5_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS6_FORWARD + "#9P" + SERVO9_POS6_FORWARD + "#21P" + SERVO21_POS6_FORWARD + "T" + travellingTime + "\r";

                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // inner class permettant de faire reculer le robot
    public static class backward extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            command = "backward";

            btn_up.setImageResource(R.drawable.ic_up);
            btn_left.setImageResource(R.drawable.ic_left);
            btn_right.setImageResource(R.drawable.ic_right);
            btn_down.setImageResource(R.drawable.ic_down_activated);
            btn_stop.setImageResource(R.drawable.ic_stop);

            // stabiliser le mobile
            stabilizeVehicle();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (command.equals("backward")) {
                String data = "#1P" + SERVO1_POS1_BACKWARD + "#9P" + SERVO9_POS1_BACKWARD + "#21P" + SERVO21_POS1_BACKWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#0P" + SERVO0_POS2_BACKWARD + "#8P" + SERVO8_POS2_BACKWARD + "#20P" + SERVO20_POS2_BACKWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS3_BACKWARD + "#9P" + SERVO9_POS3_BACKWARD + "#21P" + SERVO21_POS3_BACKWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#5P" + SERVO5_POS4_BACKWARD + "#17P" + SERVO17_POS4_BACKWARD + "#25P" + SERVO25_POS4_BACKWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#4P" + SERVO4_POS5_BACKWARD + "#16P" + SERVO16_POS5_BACKWARD + "#24P" + SERVO24_POS5_BACKWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#5P" + SERVO5_POS6_BACKWARD + "#17P" + SERVO17_POS6_BACKWARD + "#25P" + SERVO25_POS6_BACKWARD + "T" + travellingTime + "\r";

                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // inner class permettant de faire la rotation à gauche du robot
    public static class turnLeft extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            command = "turnLeft";

            btn_up.setImageResource(R.drawable.ic_up);
            btn_left.setImageResource(R.drawable.ic_left_enabled);
            btn_right.setImageResource(R.drawable.ic_right);
            btn_down.setImageResource(R.drawable.ic_down);
            btn_stop.setImageResource(R.drawable.ic_stop);

            // stabiliser le mobile
            stabilizeVehicle();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (command.equals("turnLeft")) {
                String data = "#17P" + SERVO17_POS1_TURN_LEFT + "#25P" + SERVO25_POS1_TURN_LEFT + "#5P" + SERVO5_POS1_TURN_LEFT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#16P" + SERVO16_POS2_TURN_LEFT + "#24P" + SERVO24_POS2_TURN_LEFT + "#4P" + SERVO4_POS2_TURN_LEFT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#17P" + SERVO17_POS3_TURN_LEFT + "#25P" + SERVO25_POS3_TURN_LEFT + "#5P" + SERVO5_POS3_TURN_LEFT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS4_TURN_LEFT + "#9P" + SERVO9_POS4_TURN_LEFT + "#21P" + SERVO21_POS4_TURN_LEFT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#0P" + SERVO0_POS5_TURN_LEFT + "#8P" + SERVO8_POS5_TURN_LEFT + "#20P" + SERVO20_POS5_TURN_LEFT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS6_TURN_LEFT + "#9P" + SERVO9_POS6_TURN_LEFT + "#21P" + SERVO21_POS6_TURN_LEFT + "T" + travellingTime + "\r";

                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // inner class permettant de faire la rotation à droite du robot
    public static class turnRight extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            command = "turnRight";

            btn_up.setImageResource(R.drawable.ic_up);
            btn_left.setImageResource(R.drawable.ic_left);
            btn_right.setImageResource(R.drawable.ic_right_enabled);
            btn_down.setImageResource(R.drawable.ic_down);
            btn_stop.setImageResource(R.drawable.ic_stop);

            // stabiliser le mobile
            stabilizeVehicle();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (command.equals("turnRight")) {
                String data = "#17P" + SERVO17_POS1_TURN_RIGHT + "#25P" + SERVO25_POS1_TURN_RIGHT + "#5P" + SERVO5_POS1_TURN_RIGHT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#16P" + SERVO16_POS2_TURN_RIGHT + "#24P" + SERVO24_POS2_TURN_RIGHT + "#4P" + SERVO4_POS2_TURN_RIGHT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#17P" + SERVO17_POS3_TURN_RIGHT + "#25P" + SERVO25_POS3_TURN_RIGHT + "#5P" + SERVO5_POS3_TURN_RIGHT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS4_TURN_RIGHT + "#9P" + SERVO9_POS4_TURN_RIGHT + "#21P" + SERVO21_POS4_TURN_RIGHT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#0P" + SERVO0_POS5_TURN_RIGHT + "#8P" + SERVO8_POS5_TURN_RIGHT + "#20P" + SERVO20_POS5_TURN_RIGHT + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS6_TURN_RIGHT + "#9P" + SERVO9_POS6_TURN_RIGHT + "#21P" + SERVO21_POS6_TURN_RIGHT + "T" + travellingTime + "\r";

                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);

                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    // méthode permettant d'arrêter le robot
    private void stop() {

        command = "stop";

        btn_up.setImageResource(R.drawable.ic_up);
        btn_left.setImageResource(R.drawable.ic_left);
        btn_right.setImageResource(R.drawable.ic_right);
        btn_down.setImageResource(R.drawable.ic_down);
        btn_stop.setImageResource(R.drawable.ic_stop_enabled);
    }

    // méthode permettant de stabiliser le véhicule
    private static void stabilizeVehicle() {

        // vérifier si le paramètre de stabilisation du véhicule est activé
        if (MainActivity.sharedPreferences.getBoolean("switch_calibration_vehicle", true)) {

            // position d'étalonnage des servoMoteurs
            int[] servoCalibrationPos = FilesUtils.readServoPosFromTxt();

            // vérifier l'ordre donné au mobile
            switch (command) {

                // marche avant
                case "forward":

                    // remettre les positions par défault des servoMoteurs pour la marche avant
                    resetServoPosForward();

                    // remplacer les positions des servoMoteurs par les nouvelles afin de stabiliser le mobile
                    SERVO5_POS1_FORWARD = stabilizeServoPos(SERVO5_POS1_FORWARD, servoCalibrationPos[3]);
                    SERVO17_POS1_FORWARD = stabilizeServoPos(SERVO17_POS1_FORWARD, servoCalibrationPos[7]);
                    SERVO25_POS1_FORWARD = stabilizeServoPos(SERVO25_POS1_FORWARD, servoCalibrationPos[11]);
                    SERVO4_POS2_FORWARD = stabilizeServoPos(SERVO4_POS2_FORWARD, servoCalibrationPos[3]);
                    SERVO16_POS2_FORWARD = stabilizeServoPos(SERVO16_POS2_FORWARD, servoCalibrationPos[6]);
                    SERVO24_POS2_FORWARD = stabilizeServoPos(SERVO24_POS2_FORWARD, servoCalibrationPos[10]);
                    SERVO5_POS3_FORWARD = stabilizeServoPos(SERVO5_POS3_FORWARD, servoCalibrationPos[3]);
                    SERVO17_POS3_FORWARD = stabilizeServoPos(SERVO17_POS3_FORWARD, servoCalibrationPos[7]);
                    SERVO25_POS3_FORWARD = stabilizeServoPos(SERVO25_POS3_FORWARD, servoCalibrationPos[11]);
                    SERVO1_POS4_FORWARD = stabilizeServoPos(SERVO1_POS4_FORWARD, servoCalibrationPos[1]);
                    SERVO9_POS4_FORWARD = stabilizeServoPos(SERVO9_POS4_FORWARD, servoCalibrationPos[5]);
                    SERVO21_POS4_FORWARD = stabilizeServoPos(SERVO21_POS4_FORWARD, servoCalibrationPos[9]);
                    SERVO0_POS5_FORWARD = stabilizeServoPos(SERVO0_POS5_FORWARD, servoCalibrationPos[0]);
                    SERVO8_POS5_FORWARD = stabilizeServoPos(SERVO8_POS5_FORWARD, servoCalibrationPos[4]);
                    SERVO20_POS5_FORWARD = stabilizeServoPos(SERVO20_POS5_FORWARD, servoCalibrationPos[8]);
                    SERVO1_POS6_FORWARD = stabilizeServoPos(SERVO1_POS6_FORWARD, servoCalibrationPos[1]);
                    SERVO9_POS6_FORWARD = stabilizeServoPos(SERVO9_POS6_FORWARD, servoCalibrationPos[5]);
                    SERVO21_POS6_FORWARD = stabilizeServoPos(SERVO21_POS6_FORWARD, servoCalibrationPos[9]);
                    break;

                // marche arrière
                case "backward":

                    // remettre les positions par défault des servoMoteurs pour la marche arrière
                    resetServoPosBackward();

                    // remplacer les positions des servoMoteurs par les nouvelles afin de stabiliser le mobile
                    SERVO1_POS1_BACKWARD = stabilizeServoPos(SERVO1_POS1_BACKWARD, servoCalibrationPos[1]);
                    SERVO9_POS1_BACKWARD = stabilizeServoPos(SERVO9_POS1_BACKWARD, servoCalibrationPos[5]);
                    SERVO21_POS1_BACKWARD = stabilizeServoPos(SERVO21_POS1_BACKWARD, servoCalibrationPos[9]);
                    SERVO0_POS2_BACKWARD = stabilizeServoPos(SERVO0_POS2_BACKWARD, servoCalibrationPos[0]);
                    SERVO8_POS2_BACKWARD = stabilizeServoPos(SERVO8_POS2_BACKWARD, servoCalibrationPos[4]);
                    SERVO20_POS2_BACKWARD = stabilizeServoPos(SERVO20_POS2_BACKWARD, servoCalibrationPos[8]);
                    SERVO1_POS3_BACKWARD = stabilizeServoPos(SERVO1_POS3_BACKWARD, servoCalibrationPos[1]);
                    SERVO9_POS3_BACKWARD = stabilizeServoPos(SERVO9_POS3_BACKWARD, servoCalibrationPos[5]);
                    SERVO21_POS3_BACKWARD = stabilizeServoPos(SERVO21_POS3_BACKWARD, servoCalibrationPos[9]);
                    SERVO5_POS4_BACKWARD = stabilizeServoPos(SERVO5_POS4_BACKWARD, servoCalibrationPos[3]);
                    SERVO17_POS4_BACKWARD = stabilizeServoPos(SERVO17_POS4_BACKWARD, servoCalibrationPos[7]);
                    SERVO25_POS4_BACKWARD = stabilizeServoPos(SERVO25_POS4_BACKWARD, servoCalibrationPos[11]);
                    SERVO4_POS5_BACKWARD = stabilizeServoPos(SERVO4_POS5_BACKWARD, servoCalibrationPos[2]);
                    SERVO16_POS5_BACKWARD = stabilizeServoPos(SERVO16_POS5_BACKWARD, servoCalibrationPos[6]);
                    SERVO24_POS5_BACKWARD = stabilizeServoPos(SERVO24_POS5_BACKWARD, servoCalibrationPos[10]);
                    SERVO5_POS6_BACKWARD = stabilizeServoPos(SERVO5_POS6_BACKWARD, servoCalibrationPos[3]);
                    SERVO17_POS6_BACKWARD = stabilizeServoPos(SERVO17_POS6_BACKWARD, servoCalibrationPos[7]);
                    SERVO25_POS6_BACKWARD = stabilizeServoPos(SERVO25_POS6_BACKWARD, servoCalibrationPos[11]);
                    break;

                // rotation à gauche
                case "turnLeft":
                    // remettre les positions par défault des servoMoteurs pour la rotation à droite
                    resetServoPosTurnLeft();

                    // remplacer les positions des servoMoteurs par les nouvelles afin de stabiliser le mobile
                    SERVO5_POS1_TURN_LEFT = stabilizeServoPos(SERVO5_POS1_TURN_LEFT, servoCalibrationPos[3]);
                    SERVO17_POS1_TURN_LEFT = stabilizeServoPos(SERVO17_POS1_TURN_LEFT, servoCalibrationPos[7]);
                    SERVO25_POS1_TURN_LEFT = stabilizeServoPos(SERVO25_POS1_TURN_LEFT, servoCalibrationPos[11]);
                    SERVO4_POS2_TURN_LEFT = stabilizeServoPos(SERVO4_POS2_TURN_LEFT, servoCalibrationPos[3]);
                    SERVO16_POS2_TURN_LEFT = stabilizeServoPos(SERVO16_POS2_TURN_LEFT, servoCalibrationPos[6]);
                    SERVO24_POS2_TURN_LEFT = stabilizeServoPos(SERVO24_POS2_TURN_LEFT, servoCalibrationPos[10]);
                    SERVO5_POS3_TURN_LEFT = stabilizeServoPos(SERVO5_POS3_TURN_RIGHT, servoCalibrationPos[3]);
                    SERVO17_POS3_TURN_LEFT = stabilizeServoPos(SERVO17_POS3_TURN_LEFT, servoCalibrationPos[7]);
                    SERVO25_POS3_TURN_LEFT = stabilizeServoPos(SERVO25_POS3_TURN_LEFT, servoCalibrationPos[11]);
                    SERVO1_POS4_TURN_LEFT = stabilizeServoPos(SERVO1_POS4_TURN_LEFT, servoCalibrationPos[1]);
                    SERVO9_POS4_TURN_LEFT = stabilizeServoPos(SERVO9_POS4_TURN_LEFT, servoCalibrationPos[5]);
                    SERVO21_POS4_TURN_LEFT = stabilizeServoPos(SERVO21_POS4_TURN_LEFT, servoCalibrationPos[9]);
                    SERVO0_POS5_TURN_LEFT = stabilizeServoPos(SERVO0_POS5_TURN_LEFT, servoCalibrationPos[0]);
                    SERVO8_POS5_TURN_LEFT = stabilizeServoPos(SERVO8_POS5_TURN_LEFT, servoCalibrationPos[4]);
                    SERVO20_POS5_TURN_LEFT = stabilizeServoPos(SERVO20_POS5_TURN_LEFT, servoCalibrationPos[8]);
                    SERVO1_POS6_TURN_LEFT = stabilizeServoPos(SERVO1_POS6_TURN_LEFT, servoCalibrationPos[1]);
                    SERVO9_POS6_TURN_LEFT = stabilizeServoPos(SERVO9_POS6_TURN_LEFT, servoCalibrationPos[5]);
                    SERVO21_POS6_TURN_LEFT = stabilizeServoPos(SERVO21_POS6_TURN_LEFT, servoCalibrationPos[9]);
                    break;

                // rotation à droite
                case "turnRight":
                    // remettre les positions par défault des servoMoteurs pour la rotation à droite
                    resetServoPosTurnRight();

                    // remplacer les positions des servoMoteurs par les nouvelles afin de stabiliser le mobile
                    SERVO5_POS1_TURN_RIGHT = stabilizeServoPos(SERVO5_POS1_TURN_RIGHT, servoCalibrationPos[3]);
                    SERVO17_POS1_TURN_RIGHT = stabilizeServoPos(SERVO17_POS1_TURN_RIGHT, servoCalibrationPos[7]);
                    SERVO25_POS1_TURN_RIGHT = stabilizeServoPos(SERVO25_POS1_TURN_RIGHT, servoCalibrationPos[11]);
                    SERVO4_POS2_TURN_RIGHT = stabilizeServoPos(SERVO4_POS2_TURN_RIGHT, servoCalibrationPos[3]);
                    SERVO16_POS2_TURN_RIGHT = stabilizeServoPos(SERVO16_POS2_TURN_RIGHT, servoCalibrationPos[6]);
                    SERVO24_POS2_TURN_RIGHT = stabilizeServoPos(SERVO24_POS2_TURN_RIGHT, servoCalibrationPos[10]);
                    SERVO5_POS3_TURN_RIGHT = stabilizeServoPos(SERVO5_POS3_TURN_RIGHT, servoCalibrationPos[3]);
                    SERVO17_POS3_TURN_RIGHT = stabilizeServoPos(SERVO17_POS3_TURN_RIGHT, servoCalibrationPos[7]);
                    SERVO25_POS3_TURN_RIGHT = stabilizeServoPos(SERVO25_POS3_TURN_RIGHT, servoCalibrationPos[11]);
                    SERVO1_POS4_TURN_RIGHT = stabilizeServoPos(SERVO1_POS4_TURN_RIGHT, servoCalibrationPos[1]);
                    SERVO9_POS4_TURN_RIGHT = stabilizeServoPos(SERVO9_POS4_TURN_RIGHT, servoCalibrationPos[5]);
                    SERVO21_POS4_TURN_RIGHT = stabilizeServoPos(SERVO21_POS4_TURN_RIGHT, servoCalibrationPos[9]);
                    SERVO0_POS5_TURN_RIGHT = stabilizeServoPos(SERVO0_POS5_TURN_RIGHT, servoCalibrationPos[0]);
                    SERVO8_POS5_TURN_RIGHT = stabilizeServoPos(SERVO8_POS5_TURN_RIGHT, servoCalibrationPos[4]);
                    SERVO20_POS5_TURN_RIGHT = stabilizeServoPos(SERVO20_POS5_TURN_RIGHT, servoCalibrationPos[8]);
                    SERVO1_POS6_TURN_RIGHT = stabilizeServoPos(SERVO1_POS6_TURN_RIGHT, servoCalibrationPos[1]);
                    SERVO9_POS6_TURN_RIGHT = stabilizeServoPos(SERVO9_POS6_TURN_RIGHT, servoCalibrationPos[5]);
                    SERVO21_POS6_TURN_RIGHT = stabilizeServoPos(SERVO21_POS6_TURN_RIGHT, servoCalibrationPos[9]);
                    break;
            }
            // sinon utiliser les valeurs par défault
        } else

            // vérifier l'ordre donné au mobile
            switch (command) {

                case "forward":
                    resetServoPosForward();
                    break;

                case "backward":
                    resetServoPosBackward();
                    break;

                case "turnLeft":
                    resetServoPosTurnLeft();
                    break;

                case "turnRight":
                    resetServoPosTurnRight();
                    break;
            }
    }


    // méthode permettant de modifier les positions des servoMoteurs afin de stabiliser le mobile
    private static int stabilizeServoPos(int idealPos, int calibrationPos) {

        return idealPos - (1500 - calibrationPos);
    }

    // méhode permettant de rétablir les positions des servoMoteurs par défault pour la marche avant
    private static void resetServoPosForward() {

        // position des servo moteurs lors de la marche avant
        //étape 1
        SERVO25_POS1_FORWARD = 500;
        SERVO17_POS1_FORWARD = 500;
        SERVO5_POS1_FORWARD = 2500;
        //étape 2
        SERVO24_POS2_FORWARD = 1165;
        SERVO16_POS2_FORWARD = 1110;
        SERVO4_POS2_FORWARD = 1770;
        //étape 3
        SERVO25_POS3_FORWARD = 1300;
        SERVO17_POS3_FORWARD = 1361;
        SERVO5_POS3_FORWARD = 1500;
        //étape 4
        SERVO9_POS4_FORWARD = 2500;
        SERVO21_POS4_FORWARD = 500;
        SERVO1_POS4_FORWARD = 2500;
        //étape 5
        SERVO8_POS5_FORWARD = 1930;
        SERVO20_POS5_FORWARD = 1179;
        SERVO0_POS5_FORWARD = 1830;
        //étape 6
        SERVO9_POS6_FORWARD = 1421;
        SERVO21_POS6_FORWARD = 1566;
        SERVO1_POS6_FORWARD = 1780;
    }

    // méhode permettant de rétablir les positions des servoMoteurs par défault pour la marche arrière
    private static void resetServoPosBackward() {

        // position des servo moteurs lors de la marche arrière
        //étape 1
        SERVO1_POS1_BACKWARD = 2500;
        SERVO9_POS1_BACKWARD = 2500;
        SERVO21_POS1_BACKWARD = 500;
        //étape 2
        SERVO0_POS2_BACKWARD = 1080;
        SERVO8_POS2_BACKWARD = 1180;
        SERVO20_POS2_BACKWARD = 1929;
        //étape 3
        SERVO1_POS3_BACKWARD = 1707;
        SERVO9_POS3_BACKWARD = 1421;
        SERVO21_POS3_BACKWARD = 1566;
        //étape 4
        SERVO5_POS4_BACKWARD = 2500;
        SERVO17_POS4_BACKWARD = 500;
        SERVO25_POS4_BACKWARD = 500;
        //étape 5
        SERVO4_POS5_BACKWARD = 1020;
        SERVO16_POS5_BACKWARD = 1860;
        SERVO24_POS5_BACKWARD = 1915;
        //étape 6
        SERVO5_POS6_BACKWARD = 1500;
        SERVO17_POS6_BACKWARD = 1361;
        SERVO25_POS6_BACKWARD = 1300;
    }

    // méhode permettant de rétablir les positions des servoMoteurs par défault pour la rotation à droite
    private static void resetServoPosTurnLeft() {

        // position des servo moteurs lors de la rotation à gauche
        //étape 1
        SERVO17_POS1_TURN_LEFT = 500;
        SERVO25_POS1_TURN_LEFT = 500;
        SERVO5_POS1_TURN_LEFT = 2500;
        //étape 2
        SERVO16_POS2_TURN_LEFT = 1860;
        SERVO24_POS2_TURN_LEFT = 1915;
        SERVO4_POS2_TURN_LEFT = 1820;
        //étape 3
        SERVO17_POS3_TURN_LEFT = 1361;
        SERVO25_POS3_TURN_LEFT = 1300;
        SERVO5_POS3_TURN_LEFT = 1500;
        //étape 4
        SERVO1_POS4_TURN_LEFT = 2500;
        SERVO9_POS4_TURN_LEFT = 2500;
        SERVO21_POS4_TURN_LEFT = 500;
        //étape 5
        SERVO0_POS5_TURN_LEFT = 1880;
        SERVO8_POS5_TURN_LEFT = 1980;
        SERVO20_POS5_TURN_LEFT = 1929;
        //étape 6
        SERVO1_POS6_TURN_LEFT = 1707;
        SERVO9_POS6_TURN_LEFT = 1421;
        SERVO21_POS6_TURN_LEFT = 1566;
    }

    // méhode permettant de rétablir les positions des servoMoteurs par défault pour la rotation à droite
    private static void resetServoPosTurnRight() {

        // position des servo moteurs lors de la rotation à droite
        //étape 1
        SERVO17_POS1_TURN_RIGHT = 500;
        SERVO25_POS1_TURN_RIGHT = 500;
        SERVO5_POS1_TURN_RIGHT = 2500;
        //étape 2
        SERVO16_POS2_TURN_RIGHT = 1060;
        SERVO24_POS2_TURN_RIGHT = 1160;
        SERVO4_POS2_TURN_RIGHT = 1030;
        //étape 3
        SERVO17_POS3_TURN_RIGHT = 1150;
        SERVO25_POS3_TURN_RIGHT = 1100;
        SERVO5_POS3_TURN_RIGHT = 1450;
        //étape 4
        SERVO1_POS4_TURN_RIGHT = 2500;
        SERVO9_POS4_TURN_RIGHT = 2500;
        SERVO21_POS4_TURN_RIGHT = 500;
        //étape 5
        SERVO0_POS5_TURN_RIGHT = 1000;
        SERVO8_POS5_TURN_RIGHT = 1100;
        SERVO20_POS5_TURN_RIGHT = 1050;
        //étape 6
        SERVO1_POS6_TURN_RIGHT = 1400;
        SERVO9_POS6_TURN_RIGHT = 1400;
        SERVO21_POS6_TURN_RIGHT = 1450;
    }
}