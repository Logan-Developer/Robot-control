package fr.DangerousTraveler.robotcontrol.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.SurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.DangerousTraveler.robotcontrol.CameraFragment;

// class contenant des méthodes utiles pour l'utilisation de la caméra
public class CameraUtils {

    // async task permettant d'envoyer une requête http pour dialoguer avec la caméra
    public static class SendRequestTask extends android.os.AsyncTask<Void, Void, String> {

        String urlString;
        Context context;
        SurfaceView mSurfaceView;

            // méthode permettant d'envoyer une requête HTTP pour dialoguer avec la caméra, pour lancer le streaming vidéo
            public SendRequestTask(Context appContext, String urlToUse, SurfaceView mVideoView) {

                // url à utiliser pour effectuer la requête
                urlString = urlToUse;

                context = appContext;

                // vue affichant la vidéo
                mSurfaceView = mVideoView;

            }

        // méthode permettant d'envoyer une requête HTTP pour dialoguer avec la caméra, sauf pour le streaming vidéo
        public SendRequestTask(Context appContext, String urlToUse) {

            // url à utiliser pour effectuer la requête
            urlString = urlToUse;

            context = appContext;

        }

        @Override
        protected String doInBackground(Void... voids) {

                // instancier un objet de connection http
            HttpURLConnection httpURLConnection = null;

            // essayer d'établir la connexion avec la caméra et lui envoyer la requête souhaitée
            try {

                // Créer un objet url pour effectuer la requête
                URL url = new URL(urlString);

                //établir la connexion
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // récupérer l'inputStream et le mettre dans un bufferedReader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                // mettre les données récupérées dns un stringBuilder
                StringBuilder stringBuilder = new StringBuilder();

                String line;

                // pour chaque ligne du bufferedReader, ajouter une nouvelle ligne dans la string
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuilder.append(line).append("\n");
                }

                // fermer le bufferedReader une fois son utilisation terminée
                bufferedReader.close();

                // retourner la string obtenue
                return stringBuilder.toString();

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {

                // si la connection est active, la fermer
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            // indiquer dans le logcat l'url qui a été envoyée
            Log.d("URL_CAMERA", "L'URL: " + urlString + " a été envoyée à la caméra");

            // si le status vaut 0 et que mSurfaceView n'est pas nulle, démarrer le lecteur vidéo
            if (mSurfaceView != null) {

                if (CameraUtils.checkStream(jsonString)) {

                    CameraFragment.createPlayer(context, mSurfaceView);
                }
            }
        }
    }

    // méthode permettant de vérifier si l'appareil est connecté à internet
    public static boolean isOnline(Context context) {

        // récupérer le connectivityManager à partir du service correspondant
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // récupérer les informations sur la connexion réseau
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // vérifier que l'appareil est connecté à internet
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;

        } else {

            return false;

        }
    }

    // méthode permettant de vérifier le status du streming vidéo de la caméra
    public static boolean checkStream(String jsonString) {

        // mauvais status
        int status = 999;

        // essayer de récupérer la valeur du status du streaming
        try {

            JSONObject jsonObject = new JSONObject(jsonString);

            status = jsonObject.getInt("status");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // si le status vaut 0, le streaming est opérationel, sinon, il ne l'est pas
        return status == 0;
    }
}