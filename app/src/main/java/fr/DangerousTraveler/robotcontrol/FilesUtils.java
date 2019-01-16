package fr.DangerousTraveler.robotcontrol;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;

// classe contenant des méthodes permettant de gérer les fichiers
public class FilesUtils {

    // méthode permettant de récupérer les positions d'étalonnage des servoMoteurs à partir d'un fichier texte
    public static int[] readServoPosFromTxt() {

        // récupérer le nom et l'emplacement du fichier d'étalonnage
        String path = getFilePath();
        String fileName = getFileName();

        String line;

        // positions des servoMoteurs
        int[] servoPos = {1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500};

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName + ".txt"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ( (line = bufferedReader.readLine()) != null )
            {
                if (line.startsWith("servo0:")) {

                    String[] separated = line.split(":");
                    servoPos[0] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo1:")) {

                    String[] separated = line.split(":");
                    servoPos[1] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo4:")) {

                    String[] separated = line.split(":");
                    servoPos[2] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo5:")) {

                    String[] separated = line.split(":");
                    servoPos[3] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo8:")) {

                    String[] separated = line.split(":");
                    servoPos[4] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo9:")) {

                    String[] separated = line.split(":");
                    servoPos[5] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo16:")) {

                    String[] separated = line.split(":");
                    servoPos[6] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo17:")) {

                    String[] separated = line.split(":");
                    servoPos[7] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo20:")) {

                    String[] separated = line.split(":");
                    servoPos[8] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo21:")) {

                    String[] separated = line.split(":");
                    servoPos[9] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo24:")) {

                    String[] separated = line.split(":");
                    servoPos[10] = Integer.valueOf(separated[1]);

                } else if (line.startsWith("servo25:")) {

                    String[] separated = line.split(":");
                    servoPos[11] = Integer.valueOf(separated[1]);
                }
            }
            fileInputStream.close();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("FILES", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("FILES", ex.getMessage());
        }

        return servoPos;
    }

    // vérifier que le sockage externe est accessible en lecture
    public static boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // méthode permettant de récupérer l'emplacement du fichier d'étalonnage
    public static String getFilePath() {

        String path;

        // utiliser l'emplacement par défault
        if (!MainActivity.sharedPreferences.getBoolean("switch_default_file_path", false)) {

            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/AppInventor/assets/";

        } else {

            path = MainActivity.sharedPreferences.getString("settings_custom_file_path",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/AppInventor/assets/");
        }

        return path;
    }

    // méthode permettant de récupérer le nom du fichier d'étalonnage
    public static String getFileName() {

        return MainActivity.sharedPreferences.getString("file_name", "hexapod");
    }
}
