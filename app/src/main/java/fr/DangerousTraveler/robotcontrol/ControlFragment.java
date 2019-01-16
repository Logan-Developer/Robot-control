package fr.DangerousTraveler.robotcontrol;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;

public class ControlFragment extends Fragment implements View.OnClickListener {

    private static ImageButton btn_up, btn_down, btn_left, btn_right, btn_stop;

    private static String command = "stop";

    // position des servo moteurs lors de la marche avant
    //étape 1
    private static final int SERVO1_POS1_FORWARD = 2500;
    private static final int SERVO9_POS1_FORWARD = 2500;
    private static final int SERVO21_POS1_FORWARD = 500;
    //étape 2
    private static final int SERVO0_POS2_FORWARD = 1050;
    private static final int SERVO8_POS2_FORWARD = 1280;
    private static final int SERVO20_POS2_FORWARD = 1750;
    //étape 3
    private static final int SERVO9_POS3_FORWARD = 1140;
    private static final int SERVO1_POS3_FORWARD = 1280;
    private static final int SERVO21_POS3_FORWARD = 1430;
    //étape 4
    private static final int SERVO5_POS4_FORWARD = 2500;
    private static final int SERVO25_POS4_FORWARD = 500;
    private static final int SERVO17_POS4_FORWARD = 500;
    //étape 5
    private static final int SERVO4_POS5_FORWARD = 1100;
    private static final int SERVO24_POS5_FORWARD = 1720;
    private static final int SERVO16_POS5_FORWARD = 1650;
    //étape 6
    private static final int SERVO5_POS6_FORWARD = 1240;
    private static final int SERVO17_POS6_FORWARD = 1150;
    private static final int SERVO25_POS6_FORWARD = 1150;

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

    // méthode permettant de lance l'action demandée après la reconnaissance vocale
    public static void startActionAfterSpeech() {

        switch (MainActivity.speechText) {

            case "avant":
                new forward().execute();

                break;
                case "stop":
                    new stop().execute();

                    break;
        }
    }
}