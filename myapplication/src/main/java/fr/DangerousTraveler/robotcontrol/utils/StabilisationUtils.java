package fr.DangerousTraveler.robotcontrol.utils;

import android.os.AsyncTask;

import java.util.Objects;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;

// class contenant des méthodes utiles pour la stabilisation du robot
public class StabilisationUtils {

    // valeure des axes de l'accéléromètre à l'étalonnage
    private static final int ACC_ETALON = 38;

    // méthode permettant de stabiliser le robot sur l'axe X et l'axe Y
    public static void stabilisation() {

        // récupérer la valeure du temps de déplacement depuis les paramètres de l'application
        int travellingTime = Integer.parseInt(Objects.requireNonNull(MainActivity.sharedPreferences.getString("settings_travelling_time", "2000")));

        // récupérer la valeure de l'axe X de l'accéléromètre
        BluetoothUtils.sendDataViaBluetooth("VH\r");

        // attendre avant de récupérer les données
        try {
            Thread.sleep(travellingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int accX = BluetoothUtils.getDataFromBluetooth();

        // récupérer la valeure de l'axe Y de l'accéléromètre
        BluetoothUtils.sendDataViaBluetooth("VF\r");

        // attendre avant de récupérer les données
        try {
            Thread.sleep(travellingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int accY = BluetoothUtils.getDataFromBluetooth();

        // récupérer les positions d'étalonnage des servomoteurs
        int[]servoPosEtalon = FilesUtils.readServoPosFromTxt();

        // vérifier que la stabilisation sur l'axe x est activée dans les paramètres
        if (MainActivity.sharedPreferences.getBoolean("switch_stabilisation_x", true)) {

            /* Tant que la valeure de l'axe X est plus grande que celle de l'étalon
             *  redresser le robot */
            while (accX > ACC_ETALON + 1) {

                // modifier les valeures pour stabiliser le robot
                servoPosEtalon[1]++;
                servoPosEtalon[5]--;
                servoPosEtalon[7]--;
                servoPosEtalon[11]++;

                // envoyer les nouvelles positions des servomoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth("#1P" + servoPosEtalon[1]
                        + "#9P" + servoPosEtalon[5]
                        + "#17P" + servoPosEtalon[7]
                        + "#25P" + servoPosEtalon[11]
                        + "T" + travellingTime + "\r");

                // récupérer à nouveau la valeure de l'axe X de l'accéléromètre
                BluetoothUtils.sendDataViaBluetooth("VH\r");

                // attendre avant de récupérer les données
                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                accX = BluetoothUtils.getDataFromBluetooth();
            }

            /* Tant que la valeure de l'axe X est plus petite que celle de l'étalon
             *  redresser le robot */
            while (accX < ACC_ETALON - 1) {

                // modifier les valeures pour stabiliser le robot
                servoPosEtalon[1]--;
                servoPosEtalon[5]++;
                servoPosEtalon[7]++;
                servoPosEtalon[11]--;

                // envoyer les nouvelles positions des servomoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth("#1P" + servoPosEtalon[1]
                        + "#9P" + servoPosEtalon[5]
                        + "#17P" + servoPosEtalon[7]
                        + "#25P" + servoPosEtalon[11]
                        + "T" + travellingTime + "\r");

                // récupérer à nouveau la valeure de l'axe X de l'accéléromètre
                BluetoothUtils.sendDataViaBluetooth("VH\r");

                // attendre avant de récupérer les données
                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                accX = BluetoothUtils.getDataFromBluetooth();
            }
        }

        // vérifier que la stabilisation sur l'axe y est activée dans les paramètres
        if (MainActivity.sharedPreferences.getBoolean("switch_stabilisation_y", true)) {

            // récupérer les positions d'étalonnage des servomoteurs
            servoPosEtalon = FilesUtils.readServoPosFromTxt();

            /* Tant que la valeure de l'axe Y est plus grande que celle de l'étalon
             *  redresser le robot */
            while (accY> ACC_ETALON + 1) {

                // modifier les valeures pour stabiliser le robot
                servoPosEtalon[1]++;
                servoPosEtalon[5]++;
                servoPosEtalon[7]--;
                servoPosEtalon[10]--;

                // envoyer les nouvelles positions des servomoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth("#1P" + servoPosEtalon[1]
                        + "#9P" + servoPosEtalon[5]
                        + "#17P" + servoPosEtalon[7]
                        + "#24P" + servoPosEtalon[10]
                        + "T" + travellingTime + "\r");

                // récupérer à nouveau la valeure de l'axe Y de l'accéléromètre
                BluetoothUtils.sendDataViaBluetooth("VF\r");

                // attendre avant de récupérer les données
                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                accY = BluetoothUtils.getDataFromBluetooth();
            }

            /* Tant que la valeure de l'axe Y est plus petite que celle de l'étalon
             *  redresser le robot */
            while (accY < ACC_ETALON - 1) {

                // modifier les valeures pour stabiliser le robot
                servoPosEtalon[1]--;
                servoPosEtalon[5]--;
                servoPosEtalon[7]++;
                servoPosEtalon[10]++;

                // envoyer les nouvelles positions des servomoteurs par bluetooth
                BluetoothUtils.sendDataViaBluetooth("#1P" + servoPosEtalon[1]
                        + "#9P" + servoPosEtalon[5]
                        + "#17P" + servoPosEtalon[7]
                        + "#24P" + servoPosEtalon[10]
                        + "T" + travellingTime + "\r");

                // récupérer à nouveau la valeure de l'axe Y de l'accéléromètre
                BluetoothUtils.sendDataViaBluetooth("VF\r");

                // attendre avant de récupérer les données
                try {
                    Thread.sleep(travellingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                accY = BluetoothUtils.getDataFromBluetooth();
            }
        }
    }

    // inner class permettant d'effectuer la stabilisation du robot dans une tâche de fond
    public static class StabilisationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            stabilisation();
            return null;
        }
    }
}