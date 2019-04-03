package fr.DangerousTraveler.robotcontrol.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.app.NavUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.DangerousTraveler.robotcontrol.CameraFragment;
import fr.DangerousTraveler.robotcontrol.R;
import fr.DangerousTraveler.robotcontrol.utils.CameraUtils;

public class PreviewActivity extends AppCompatActivity {

    // booléen permettant de masquer automatiquement l'interface
    private static final boolean AUTO_HIDE = true;

    // booléen permettant de savoir si un enregistrement vidéo est en cours
    private boolean isRecording = false;

    // temps avant de masquer l'interface
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private SurfaceView mSurfaceView;

    private FloatingActionButton mRecordFab;

    // délai d'attente avant de jouer l'animation visant à masquer l'interface
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            // tâche pour masquer l'interface
            mSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            // tâche pour afficher l'interface
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    // masquer ou afficher l'interface lors du click sur l'écran
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mSurfaceView = findViewById(R.id.media_player);
        mRecordFab = findViewById(R.id.fab_record);

        // mettre le fond du fraggent transparent, afin de voir la preview
        findViewById(R.id.fragment).setBackgroundResource(R.color.transparent);

        // interaction de l'utilisateur pour afficher ou masquer l'interface
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        findViewById(R.id.btn_quit_preview_screen).setOnTouchListener(mDelayHideTouchListener);

        // démarrer le stream vidéo
        CameraFragment.launchStream(this, mSurfaceView);
    }

    @Override
    public void onResume() {
        super.onResume();

        // relancer la lecture du flux vidéo
        CameraFragment.launchStream(this, mSurfaceView);
    }

    @Override
    public void onPause() {
        super.onPause();

        // arrêter le lecteur vidéo
        CameraFragment.releasePlayer(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // arrêter le lecteur vidéo
        CameraFragment.releasePlayer(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // attendre un moment avant de masquer l'interface
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // revenir en arrière lors du click sur le bouton retour
        if (id == android.R.id.home) {

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // afficher ou masquer l'interface
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    // masquer l'interface
    private void hide() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // attendre avant de masquer l'interface
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {

        // afficher la statusBar
        mSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // attendre avant d'afficher l'interface
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    // attendre avant de masquer l'interface
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // méthode permettant de fermer la previewActivity lors du click sur le bouton correspondant
    public void onClickQuitButton(View view) {

        finish();
    }

    // méthode permettant de lancer ou arrêter l'enregistrement de la vidéo lors duclick sur le bouton correspondant
    public void onClickRecordButton(View view) {

        // vérifier que l'appareil est connecté à internet
        if (CameraUtils.isOnline(this)) {

            // si aucun enregistrement n'est en cours, en lancer un
            if (!isRecording) {

                // arrêter le lecteur vidéo
                CameraFragment.releasePlayer(this);

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // mettre la caméra en mode vidéo
                new CameraUtils.SendRequestTask(this, "http://10.5.5.9/gp/gpControl/command/mode?p=0");

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // choisir la résolution et le frame rate de la vidéo selon les paramètres entrés par l'utilisateur
                new CameraUtils.SendRequestTask(this,
                        MainActivity.sharedPreferences.getString("video_resolution_key", "http://10.5.5.9/gp/gpControl/setting/2/9"));

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new CameraUtils.SendRequestTask(this,
                        MainActivity.sharedPreferences.getString("video_frame_rate_key", "http://10.5.5.9/gp/gpControl/setting/3/5"));

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // lancer la capture de la vidéo
                new CameraUtils.SendRequestTask(this, "http://10.5.5.9/gp/gpControl/command/shutter?p=1");

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // indiquer qu'un enregistrement est en cours
                isRecording = true;
                mRecordFab.setImageResource(R.drawable.ic_stop);

                // sinon arrêter la capture de la vidéo
            } else {

                new CameraUtils.SendRequestTask(this, "http://10.5.5.9/gp/gpControl/command/shutter?p=0");

                // indiquer qu'aucun enregistrement n'est en cours
                isRecording = false;
                mRecordFab.setImageResource(R.drawable.ic_record);

                // patienter 100 ms avant de renvoyer d'autres instruction
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // relancer la lecture de la vidéo
                CameraFragment.launchStream(this, mSurfaceView);
            }

        // sinon afficher un message d'erreur
        } else {

            Toast.makeText(this, getString(R.string.error_msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    // méthode permettant de prendre une photo lors du click sur le bouton correspondant
    public void onClickTakePhotoButton(View view) {

        // vérifier que l'appareil est connecté à internet
        if (CameraUtils.isOnline(this)) {

            // arrêter le lecteur vidéo
            CameraFragment.releasePlayer(this);

            // mettre la caméra en mode photo
            new CameraUtils.SendRequestTask(this, "http://10.5.5.9/gp/gpControl/command/mode?p=1");

            // patienter 100 ms avant de renvoyer d'autres instruction
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // prendre la photo
            new CameraUtils.SendRequestTask(this, "http://10.5.5.9/gp/gpControl/command/shutter?p=1");

        // sinon afficher un message d'erreur
        } else {

            Toast.makeText(this, getString(R.string.error_msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}
