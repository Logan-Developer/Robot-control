package fr.DangerousTraveler.robotcontrol;

import android.bluetooth.BluetoothAdapter;

import java.io.IOException;
import java.util.Arrays;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;

// classe contenant des méthodes permettant de gérer les connexions bluetooth
public class BluetoothUtils {

    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // méthode permettant de vérifier la disponibilité du bluetooth sur l'appareil
    public static boolean isBluetoothAvailable() {

        return mBluetoothAdapter != null;
    }

    // méthode permettant d'envoyer les positions des servoMoteurs par bluetooth
    public static void sendDataViaBluetooth(String data) {

        try {
            MainActivity.bluetoothSocket.getOutputStream().write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // méthode permettant d'initialiser les servoMoteurs à leur position d'étalonnage
    public static String initServoPos() {

        String travellingTime = MainActivity.sharedPreferences.getString("settings_travelling_time", "2000");

        // récupérer les positions des servoMoteurs à partir du fichier txt
        int[] servoPos = FilesUtils.readServoPosFromTxt();

        // mettre les données à envoyer dans une String
        String data = "#0P" + servoPos[0]
                + "#1P" + servoPos[1]
                + "#4P" + servoPos[2]
                + "#5P" + servoPos[3]
                + "#8P" + servoPos[4]
                + "#9P" + servoPos[5]
                + "#16P" + servoPos[6]
                + "#17P" + servoPos[7]
                + "#20P" + servoPos[8]
                + "#21P" + servoPos[9]
                + "#24P" + servoPos[10]
                + "#25P" + servoPos[11]
                + "T" + travellingTime + "\r";

        return data;
    }
}