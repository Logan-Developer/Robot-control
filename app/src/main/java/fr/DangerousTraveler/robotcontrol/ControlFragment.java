package fr.DangerousTraveler.robotcontrol;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Objects;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;
import fr.DangerousTraveler.robotcontrol.utils.BluetoothUtils;
import fr.DangerousTraveler.robotcontrol.utils.FilesUtils;

public class ControlFragment extends Fragment implements View.OnClickListener {

    private static ImageButton btn_up, btn_down, btn_left, btn_right, btn_stop;

    private static String command = "stop";

    // position des servo moteurs lors de la marche avant
    //étape 1
    private static int SERVO1_POS1_FORWARD = 2500;
    private static int SERVO9_POS1_FORWARD = 2500;
    private static int SERVO21_POS1_FORWARD = 500;
    //étape 2
    private static int SERVO0_POS2_FORWARD = 1050;
    private static int SERVO8_POS2_FORWARD = 1280;
    private static int SERVO20_POS2_FORWARD = 1750;
    //étape 3
    private static int SERVO9_POS3_FORWARD = 1140;
    private static int SERVO1_POS3_FORWARD = 1280;
    private static int SERVO21_POS3_FORWARD = 1430;
    //étape 4
    private static int SERVO5_POS4_FORWARD = 2500;
    private static int SERVO25_POS4_FORWARD = 500;
    private static int SERVO17_POS4_FORWARD = 500;
    //étape 5
    private static int SERVO4_POS5_FORWARD = 1100;
    private static int SERVO24_POS5_FORWARD = 1720;
    private static int SERVO16_POS5_FORWARD = 1650;
    //étape 6
    private static int SERVO5_POS6_FORWARD = 1240;
    private static int SERVO17_POS6_FORWARD = 1150;
    private static int SERVO25_POS6_FORWARD = 1150;

    // String contenant les positions d'étalonnage des servoMoteurs
    private static String calibrationServoPos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_control, container, false);

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

        // mettre les positions d'étalonnage des servoMoteurs dans une String
        calibrationServoPos = BluetoothUtils.initServoPos();

        return rootView;
    }

    // gérer les clicks sur les boutons de contrôle
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            // faire avancer le robot
            case R.id.btn_up:

             // vérifier que l'on est connecté au robot
             if (MainActivity.bluetoothConnected) {

                 // faire avancer le robot
                 new forward().execute();
             }
             break;

            // arrêter le robot
            case R.id.btn_stop:

                // vérifier que l'on est connecté au robot
                if (MainActivity.bluetoothConnected) {

                    // faire avancer le robot
                    new stop().execute();
                }
                break;
        }
    }

    // inner class permettant de faire avancer le robot
    public static class forward extends AsyncTask<Void, Void, Void> {

        // récupérer les valeurs de temps
        String travellingTime = MainActivity.sharedPreferences.getString("settings_travelling_time", "2000");
        long timeBetweenSteps = Long.parseLong(Objects.requireNonNull(MainActivity.sharedPreferences.getString("settings_time_steps", "2000")));

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
                String data = "#1P" + SERVO1_POS1_FORWARD + "#9P" + SERVO9_POS1_FORWARD + "#21P" + SERVO21_POS1_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#0P" + SERVO0_POS2_FORWARD + "#8P" + SERVO8_POS2_FORWARD + "#20P" + SERVO20_POS2_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#1P" + SERVO1_POS3_FORWARD + "#9P" + SERVO9_POS3_FORWARD + "#21P" + SERVO21_POS3_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#5P" + SERVO5_POS4_FORWARD + "#17P" + SERVO17_POS4_FORWARD + "#25P" + SERVO25_POS4_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#4P" + SERVO4_POS5_FORWARD + "#16P" + SERVO16_POS5_FORWARD + "#24P" + SERVO24_POS5_FORWARD + "T" + travellingTime + "\r";
                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data = "#5P" + SERVO5_POS6_FORWARD + "#17P" + SERVO17_POS6_FORWARD + "#25P" + SERVO25_POS6_FORWARD + "T" + travellingTime + "\r";

                // envoyer les positions des servoMoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth(data);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // remettre les servoMoteurs à leur position d'étalonnage
                BluetoothUtils.sendDataViaBluetooth(calibrationServoPos);

                try {
                    Thread.sleep(timeBetweenSteps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // inner class permettant d'arrêter le robot
    public static class stop extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            command = "stop";

            btn_up.setImageResource(R.drawable.ic_up);
            btn_left.setImageResource(R.drawable.ic_left);
            btn_right.setImageResource(R.drawable.ic_right);
            btn_down.setImageResource(R.drawable.ic_down);
            btn_stop.setImageResource(R.drawable.ic_stop_enabled);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

    // méthode permettant de lancer l'action demandée après la reconnaissance vocale
    public static void startActionAfterSpeech() {

        switch (MainActivity.speechText) {

            case "avant":

                // lancer la marche avant
                new forward().execute();

                break;
                case "stop":

                    // arrêter le mobile
                    new stop().execute();

                    break;
        }
    }

    // méthode permettant de stabiliser le véhicule
    private static void stabilizeVehicle() {

        // vérifier si le paramètre de stabilisation du véhicule est activé
        if (MainActivity.sharedPreferences.getBoolean("switch_calibration_vehicle", true)) {

            // position d'étalonnage des servoMoteurs
            int[] servoCalibrationPos = FilesUtils.readServoPosFromTxt();

            // remettre les positions par défault des servoMoteurs pour la marche avant
            resetServoPosForward();

            // remplacer les positions des servoMoteurs par les nouvelles afin de stabiliser le mobile
            SERVO1_POS1_FORWARD = stabilizeServoPos(SERVO1_POS1_FORWARD, servoCalibrationPos[1]);
            SERVO9_POS1_FORWARD = stabilizeServoPos(SERVO9_POS1_FORWARD, servoCalibrationPos[5]);
            SERVO21_POS1_FORWARD = stabilizeServoPos(SERVO21_POS1_FORWARD, servoCalibrationPos[9]);
            SERVO0_POS2_FORWARD = stabilizeServoPos(SERVO0_POS2_FORWARD, servoCalibrationPos[0]);
            SERVO8_POS2_FORWARD = stabilizeServoPos(SERVO8_POS2_FORWARD, servoCalibrationPos[4]);
            SERVO20_POS2_FORWARD = stabilizeServoPos(SERVO20_POS2_FORWARD, servoCalibrationPos[8]);
            SERVO1_POS3_FORWARD = stabilizeServoPos(SERVO1_POS3_FORWARD, servoCalibrationPos[1]);
            SERVO9_POS3_FORWARD = stabilizeServoPos(SERVO9_POS3_FORWARD, servoCalibrationPos[5]);
            SERVO21_POS3_FORWARD = stabilizeServoPos(SERVO21_POS3_FORWARD, servoCalibrationPos[9]);
            SERVO5_POS4_FORWARD = stabilizeServoPos(SERVO5_POS4_FORWARD, servoCalibrationPos[3]);
            SERVO17_POS4_FORWARD = stabilizeServoPos(SERVO17_POS4_FORWARD, servoCalibrationPos[7]);
            SERVO25_POS4_FORWARD = stabilizeServoPos(SERVO25_POS4_FORWARD, servoCalibrationPos[11]);
            SERVO4_POS5_FORWARD = stabilizeServoPos(SERVO4_POS5_FORWARD, servoCalibrationPos[3]);
            SERVO16_POS5_FORWARD = stabilizeServoPos(SERVO16_POS5_FORWARD, servoCalibrationPos[6]);
            SERVO24_POS5_FORWARD = stabilizeServoPos(SERVO24_POS5_FORWARD, servoCalibrationPos[10]);
            SERVO5_POS6_FORWARD = stabilizeServoPos(SERVO5_POS6_FORWARD, servoCalibrationPos[3]);
            SERVO17_POS6_FORWARD = stabilizeServoPos(SERVO17_POS6_FORWARD, servoCalibrationPos[7]);
            SERVO25_POS6_FORWARD = stabilizeServoPos(SERVO25_POS6_FORWARD, servoCalibrationPos[11]);

        // sinon utiliser les valeurs par défault
        } else
            resetServoPosForward();
    }

    // méthode permettant de modifier les positions des servoMoteurs afin de stabiliser le mobile
    private static int stabilizeServoPos(int idealPos, int calibrationPos) {

        return idealPos - (1500 - calibrationPos);
    }

    // méhode permettant de rétablir les positions des servoMoteurs par défault pour la marche avant
    private static void resetServoPosForward() {

        // position des servo moteurs lors de la marche avant
        //étape 1
        SERVO1_POS1_FORWARD = 2500;
        SERVO9_POS1_FORWARD = 2500;
        SERVO21_POS1_FORWARD = 500;
        //étape 2
        SERVO0_POS2_FORWARD = 1050;
        SERVO8_POS2_FORWARD = 1280;
        SERVO20_POS2_FORWARD = 1750;
        //étape 3
        SERVO9_POS3_FORWARD = 1140;
        SERVO1_POS3_FORWARD = 1280;
        SERVO21_POS3_FORWARD = 1430;
        //étape 4
        SERVO5_POS4_FORWARD = 2500;
        SERVO25_POS4_FORWARD = 500;
        SERVO17_POS4_FORWARD = 500;
        //étape 5
        SERVO4_POS5_FORWARD = 1100;
        SERVO24_POS5_FORWARD = 1720;
        SERVO16_POS5_FORWARD = 1650;
        //étape 6
        SERVO5_POS6_FORWARD = 1240;
        SERVO17_POS6_FORWARD = 1150;
        SERVO25_POS6_FORWARD = 1150;
    }
}