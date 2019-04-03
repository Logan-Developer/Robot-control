package fr.DangerousTraveler.robotcontrol;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Objects;

import fr.DangerousTraveler.robotcontrol.activities.MainActivity;
import fr.DangerousTraveler.robotcontrol.activities.PreviewActivity;
import fr.DangerousTraveler.robotcontrol.utils.CameraUtils;

public class CameraFragment extends Fragment {

    // vue affichant la preview de la caméra
    public static SurfaceView mSurfaceView;

    // lecteur vidéo
    private static LibVLC libVLC;
    private static MediaPlayer mediaPlayer;

    private ImageButton mFullscreenButton;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        mSurfaceView = rootView.findViewById(R.id.media_player);
        mFullscreenButton = rootView.findViewById(R.id.btn_fullscreen);

        // si le thème foncé est désactivé, changer la couleur des éléments
        if (!MainActivity.sharedPreferences.getBoolean("dark_theme_key", false)) {

            mSurfaceView.setBackgroundResource(R.color.colorPrimary);
            mFullscreenButton.setBackgroundResource(R.color.colorPrimary);
            mFullscreenButton.setImageResource(R.drawable.ic_fullscreen);
        }

        // lancer la preview de la caméra en plein écran lors du click sur le bouton correspondant
        mFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), PreviewActivity.class);
                startActivity(intent);
            }
        });

        // démarrer la lecture du flux vidéo
        launchStream(getContext(), mSurfaceView);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // relancer la lecture du flux vidéo
        launchStream(getContext(), mSurfaceView);
    }

    @Override
    public void onPause() {
        super.onPause();

        // arrêter le lecteur vidéo
        releasePlayer(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // arrêter le lecteur vidéo
        releasePlayer(getContext());
    }

    // méthode permettant d'arrêter le lecteur vidéo
    public static void releasePlayer(Context context) {

        // arrêter la diffusion de la vidéo
        new CameraUtils.SendRequestTask(context, "http://10.5.5.9/gp/gpControl/execute?p1=gpStream&amp;a1=proto_v2&amp;c1=stop");

        // si libVLC est déjà nul, ne rien faire
        if (libVLC == null)
            return;

        // arrêter le lecteur vidéo
        mediaPlayer.stop();

        // détâcher la sortie vidéo de la surfaceView
        final IVLCVout vout = mediaPlayer.getVLCVout();
        vout.detachViews();

        // arrêter libVLC
        libVLC.release();
        libVLC = null;
    }

    // méthode permettant de démarrer la lecture du flux udp
    public static void launchStream(Context context, SurfaceView mVideoView) {

        // vérifier que l'appareil est connecté à internet
        if (CameraUtils.isOnline(context)) {

           /*// définir les paramètres de bitrate et de résolution du streaming vidéo
            new CameraUtils.SendRequestTask(context, MainActivity.sharedPreferences.getString("streaming_bitrate_key",
                    "http://10.5.5.9/gp/gpControl/setting/62/250000")).execute();

            // patienter 100 ms avant de renvoyer d'autres instruction
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new CameraUtils.SendRequestTask(context, MainActivity.sharedPreferences.getString("streaming_resolution_key",
                    "http://10.5.5.9/gp/gpControl/setting/64/0")).execute();

            // patienter 100 ms avant de renvoyer d'autres instruction
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // envoyer l'url définie dans les paramètres si l'utilisateur le souhaite
            if (MainActivity.sharedPreferences.getBoolean("send_url_switch_key", true))
                new CameraUtils.SendRequestTask(context, MainActivity.sharedPreferences.getString("url_to_send_key",
                        "http://10.5.5.9/gp/gpControl/execute?p1=gpStream&amp;a1=proto_v2&amp;c1=start"), mVideoView).execute(); */

            createPlayer(context, mVideoView);

        }  // sinon afficher un message d'erreur
        else {
            Toast.makeText(context, context.getString(R.string.error_msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    // méthode permettant de créer le lecteur vidéo affichant le stream udp récupéré
    public static void createPlayer(Context context, SurfaceView videoView) {

        // array contenant les différents paramètres à prendre en compte pour le streaming vidéo
        ArrayList<String> options = new ArrayList<>();
        options.add("--sout=\"#transcode{vcodec=h264}:rtp{dst=127.0.0.1,port=4444,sdp=rtsp://0.0.0.0:8080/live.sdp}\"");

        // instancier libVLC
        libVLC = new LibVLC(Objects.requireNonNull(context), options);

        // créer le lecteur vidéo
        mediaPlayer = new MediaPlayer(libVLC);

        // définir la taille de la vidéo
        mediaPlayer.setScale(Float.parseFloat(Objects.requireNonNull(MainActivity.sharedPreferences.getString("video_scale_key", "1"))));

        // définir la sortie vidéo sur la surfaceView
        final IVLCVout vout = mediaPlayer.getVLCVout();
        vout.setVideoView(videoView);
        vout.attachViews();

        // définir le média à afficher
        Media media = new Media(libVLC, Uri.parse(MainActivity.sharedPreferences.getString("udp_stream_adress_key", "udp://@:8554")));

        // indiquer au lecteur vidéo de lire ce média
        mediaPlayer.setMedia(media);
        mediaPlayer.play();
    }
}