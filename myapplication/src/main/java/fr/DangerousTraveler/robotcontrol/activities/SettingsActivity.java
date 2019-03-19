package fr.DangerousTraveler.robotcontrol.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import fr.DangerousTraveler.robotcontrol.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rootLayout = findViewById(R.id.settings);

        // instancier les sharedPreferences
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        // choisir le thème à afficher
        setTheme(actionBar);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    // méthode permettant de choisir le thème à utiliser
    private void setTheme(ActionBar actionBar) {

        // vérifier si le thème foncé est activé dans les paramètres
        if (sharedPreferences.getBoolean("dark_theme_key", false)) {

            // mettre un fond foncé
            rootLayout.setBackgroundResource(R.color.colorPrimaryDark_DarkMode);

            // appliquer le thème foncé
            setTheme(R.style.SettingsThemeDark);

            // mettre l'actionBar en foncé
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDarkMode)));
        }
    }
}