package fr.DangerousTraveler.robotcontrol.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import fr.DangerousTraveler.robotcontrol.ControlFragment;
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

        return "#0P" + servoPos[0]
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
    }

    // méthode permettant de récupérer les valeurs des accéléromètre par bluetooth
    public static int getDataFromBluetooth() {

        // inputStream contenant les données récupérées
        InputStream inputStream = null;

        // nombre d'octets récupérés
        int byteCount;

        // données récupérées sous forme de string
        int data = 38;

        // essayer de récupérer les données
        try {
            inputStream = MainActivity.bluetoothSocket.getInputStream();
            byteCount = inputStream.available();

            // si le nombre d'octet est supérieur à 0, convertir les données en string en utilisant l'encodage UTF-8
            if(byteCount > 0)
            {
                byte[] rawBytes = new byte[byteCount];
                inputStream.read(rawBytes);
                String dataString = new String(rawBytes, StandardCharsets.UTF_8);

                // enlever les retour àla ligne de la string pour pouvoir la convertir en integer
                dataString = dataString.replace("\r", "");
                dataString = dataString.replace("\n", "");

                // convertir la string en integer si c'est possible, sinon retourner 38 et afficher une erreur dans le logcat
                try {
                    data = Integer.valueOf(dataString);

                } catch (NumberFormatException e) {

                    Log.e("STABILISATION", "Impossible to format the String: \"" + dataString + "\" into integer.");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        // retourner les données récupérées sous forme de string
        return data;
    }

    // méthode permettant d'initialiser les entrées analogiques du robot
    public static void initialiseAnalogInputs() {

        sendDataViaBluetooth("VF\r");
        sendDataViaBluetooth("VH\r");
    }
}