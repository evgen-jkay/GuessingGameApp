package com.jkay.guessinggameapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * @author Eugen Landarenko
 * @version 1.1
 * @since verCode 2
 */
public class MainActivity extends AppCompatActivity {
    private EditText txtGuess;
    private Button btnGuess;
    private TextView lblOutput;
    private TextView lblRange;
    private int theNumber;
    private int range = 100;
    private int numberOfTries;
    private int maxTries = 7;

    /**
     * Game logic.
     *
     * @since verCode 1
     */
    public void checkGuess() {
        String message = "";

        try {
            int guess = Integer.parseInt(txtGuess.getText().toString());
            numberOfTries++;
            if (guess < theNumber)
                message = guess + getString(R.string.isLow);
            else if (guess > theNumber)
                message = guess + getString(R.string.isHigh);
            else {
                message = guess + getString(R.string.isCorrect) + numberOfTries + getString(R.string.tries);
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                int gamesWon = preferences.getInt("gamesWon", 0) + 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("gamesWon", gamesWon);
                editor.apply();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                newGame();
            }
        } catch (Exception e) {
            message = getString(R.string.textView3) + range + ".";
        } finally {
            if (numberOfTries >= maxTries) {
                message = getString(R.string.isLoss) + theNumber + getString(R.string.wasTheNumber);
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                int gamesLost = preferences.getInt("gamesLost", 0) + 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("gamesLost", gamesLost);
                editor.apply();
            }
            lblOutput.setText(message);
            txtGuess.requestFocus();
            txtGuess.selectAll();
        }
    }

    /**
     * New Game Start.
     *
     * @since verCode 1
     */
    public void newGame() {
        theNumber = (int) (Math.random() * range + 1);
        maxTries = (int) (Math.log(range) / Math.log(2) + 1);
        numberOfTries = 0;
        lblRange.setText(getString(R.string.textView3) + range + ".");
        txtGuess.setText("" + range / 2);
        txtGuess.requestFocus();
        txtGuess.selectAll();
    }

    /**
     * Create Options Menu.
     *
     * @since verCode 1
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Options Item Selected.
     *
     * @since verCode 2
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Settings Gama
            case R.id.action_settings:
                final CharSequence[] items = {
                        getString(R.string.to10),
                        getString(R.string.to100),
                        getString(R.string.to1000)
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.theRange));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        switch (item) {
                            case 0:
                                range = 10;
                                storeRange(10);
                                newGame();
                                break;
                            case 1:
                                range = 100;
                                storeRange(100);
                                newGame();
                                break;
                            case 2:
                                range = 1000;
                                storeRange(1000);
                                newGame();
                                break;
                        }
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            // New Game Start
            case R.id.action_new_game:
                newGame();
                return true;
            // Game Statistic
            case R.id.action_game_stats:
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                int gamesWon = preferences.getInt("gamesWon", 0);
                int gamesLost = preferences.getInt("gamesLost", 0);
                int total = gamesLost + gamesWon;
                int percent = Math.round((gamesWon * 100.0f) / (total));
                AlertDialog stateDialog = new AlertDialog.Builder(MainActivity.this).create();
                stateDialog.setTitle(getString(R.string.game_stats));
                stateDialog.setMessage(getString(R.string.textStats) + gamesWon
                        + getString(R.string.textStats1) + total
                        + getString(R.string.textStats2) + percent + "%!");
                stateDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                stateDialog.show();
                return true;
            // About
            case R.id.action_about:
                AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                aboutDialog.setTitle(getString(R.string.about) + " " + getString(R.string.app_name));
                aboutDialog.setMessage("(c)2022 " + getString(R.string.author));
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                aboutDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Rang in game.
     *
     * @param newRange
     * @since verCode 1
     */
    public void storeRange(int newRange) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("range", newRange);
        editor.apply();
    }


    /**
     * Main method.
     *
     * @param savedInstanceState
     *
     * @since verCode 2
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AdMob start
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // end AdMob

        txtGuess = (EditText) findViewById(R.id.txtGuess);
        btnGuess = (Button) findViewById(R.id.btnGuess);
        lblOutput = (TextView) findViewById(R.id.lblOutput);
        lblRange = (TextView) findViewById(R.id.textView2);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        range = preferences.getInt("range", 100);
        maxTries = (int) (Math.log(range) / Math.log(2) + 1);
        newGame();
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });
        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkGuess();
                return true;
            }
        });
    }
}