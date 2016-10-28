package com.futurelanguagelearning.languagetrainerandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.futurelanguagelearning.languagetrainerandroid";

    private TextView termLabel;
    private TextView romanizationLabel;
    private TextView sentenceLabel;
    private TextView feedbackLabel;

    private Button answ1Button;
    private Button answ2Button;
    private Button answ3Button;
    private Button answ4Button;
    private Button redButton;
    private Button greenButton;
    private Button continueButton;
    private TrainerFct trainer;

    private String currently_correct_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trainer = new TrainerFct();
        trainer.readLWTExportFile();

        Log.i(TAG, "onCreate");

        termLabel = (TextView) findViewById(R.id.termLabel);
        romanizationLabel = (TextView) findViewById(R.id.romanizationLabel);
        sentenceLabel = (TextView) findViewById(R.id.sentenceLabel);
        feedbackLabel = (TextView) findViewById(R.id.feedbackLabel);

        answ1Button = (Button) findViewById(R.id.answ1Button);
        answ2Button = (Button) findViewById(R.id.answ2Button);
        answ3Button = (Button) findViewById(R.id.answ3Button);
        answ4Button = (Button) findViewById(R.id.answ4Button);
        redButton = (Button) findViewById(R.id.redButton);
        greenButton = (Button) findViewById(R.id.greenButton);
        continueButton = (Button) findViewById(R.id.continueButton);

        Button[] Buttons = {answ1Button, answ2Button, answ3Button, answ4Button, greenButton, redButton};

        for (Button button : Buttons) {
            button.setEnabled(false);
            button.setBackgroundResource(android.R.drawable.btn_default);
        }
        redButton.setBackgroundColor(Color.TRANSPARENT);
        greenButton.setBackgroundColor(Color.TRANSPARENT);

        termLabel.setText("Welcome | Hallo | Привет | 你好");
        sentenceLabel.setText("");
        romanizationLabel.setText("");
        feedbackLabel.setText("Ready when you are :)");
        continueButton.setText("Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trainer = new TrainerFct();
        trainer.readLWTExportFile();

        if (continueButton.getText().toString().equals("Continue") && continueButton.isEnabled() && feedbackLabel.getText().toString().equals("Click to save changes.")) {
            sentenceLabel.setText(trainer.getSentence(termLabel.getText().toString()));
            romanizationLabel.setText(trainer.getRomanization(termLabel.getText().toString()));

            Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};

            for (Button button : answerButtons) {
                if (button.getText().toString().equals(currently_correct_answer)) {
                    button.setText(trainer.getTranslation(termLabel.getText().toString()));
                }
            }

            currently_correct_answer = trainer.getTranslation(termLabel.getText().toString());
            feedbackLabel.setText("Changes saved.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.changeWordProperties || id == R.id.moveToWellKnown || id == R.id.moveToIgnore) {
            if(continueButton.getText().toString().equals("Start")) {
                feedbackLabel.setText("No term selected. Start LT.");
            } else if (continueButton.isEnabled() == false) {
                Toast.makeText(this, "Select a final answer first.", Toast.LENGTH_SHORT).show();
            } else {

                if (id == R.id.changeWordProperties) {
                    Toast.makeText(this, "Loading word properties..", Toast.LENGTH_SHORT).show();

                    (new Handler())
                            .postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            Intent i = new Intent(MainActivity.this, Pop.class);
                                            String term = termLabel.getText().toString();

                                            Bundle bundle = new Bundle();
                                            bundle.putString("currentterm", term);
                                            i.putExtras(bundle);

                                            startActivity(i);
                                            feedbackLabel.setText("Click to save changes.");
                                        }
                                    }, 1000);
                } else if(id == R.id.moveToWellKnown) {
                    trainer.changeStatus(termLabel.getText().toString(), 99);
                    Button[] Buttons = {answ1Button, answ2Button, answ3Button, answ4Button, redButton, greenButton};

                    for (Button button : Buttons) {
                        button.setEnabled(false);
                    }

                    continueButton.setEnabled(true);
                    redButton.setBackgroundColor(Color.TRANSPARENT);
                    greenButton.setBackgroundColor(Color.TRANSPARENT);

                    try {
                        trainer.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    feedbackLabel.setText("Moved to Well Known.");
                } else {
                    trainer.changeStatus(termLabel.getText().toString(), 98);
                    Button[] Buttons = {answ1Button, answ2Button, answ3Button, answ4Button, redButton, greenButton};

                    for (Button button : Buttons) {
                        button.setEnabled(false);
                    }

                    continueButton.setEnabled(true);
                    redButton.setBackgroundColor(Color.TRANSPARENT);
                    greenButton.setBackgroundColor(Color.TRANSPARENT);

                    try {
                        trainer.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    feedbackLabel.setText("Term will be ignored.");
                }
            }
        } else {
            if (id == R.id.syncData) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            trainer.createImportFiles();
                        } catch (IOException e) {
                            Log.v(TAG,e.toString());
                        }
                        trainer = new TrainerFct();
                        trainer.readLWTExportFile();
                    }
                };

                Thread thread = new Thread(r);
                thread.start();
                Toast.makeText(this,"Synchronizing..",Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void continueButtonClicked(View view) {
        termLabel.setText(trainer.randomTermFromDatabase());
        sentenceLabel.setText(trainer.getSentence(termLabel.getText().toString()));
        feedbackLabel.setText("Do you know the answer? Choose green or red.");

        Button[] answButtons = {answ1Button, answ2Button, answ3Button, answ4Button};
        for (Button button : answButtons) {
            button.setEnabled(false);
            button.setText("");
            button.setBackgroundResource(android.R.drawable.btn_default);
        }
        continueButton.setEnabled(false);
        redButton.setEnabled(true);
        greenButton.setEnabled(true);
        continueButton.setText("Continue");

        redButton.setBackgroundColor(Color.RED);
        greenButton.setBackgroundColor(Color.GREEN);
        romanizationLabel.setBackgroundResource(R.color.grey);

        romanizationLabel.setText("");

        currently_correct_answer = trainer.getTranslation(termLabel.getText().toString());
    }

    public void redButtonClicked(View view) throws IOException {
        if (feedbackLabel.getText().toString().equals("Did you know the answer? Choose green or red.")) {
            redButton.setBackgroundColor(Color.TRANSPARENT);
            greenButton.setBackgroundColor(Color.TRANSPARENT);
            continueButton.setEnabled(true);
            redButton.setEnabled(false);
            greenButton.setEnabled(false);

            Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};

            for (Button button : answerButtons) {
                if (trainer.getTranslation(termLabel.getText().toString()).equals(button.getText().toString()))
                    button.setBackgroundColor(Color.RED);
            }

            if(Integer.parseInt(trainer.getStatus(termLabel.getText().toString())) == 1) {
                feedbackLabel.setText("Status unchanged (1)");
            } else {
                int before = Integer.parseInt(trainer.getStatus(termLabel.getText().toString()));
                int after = before - 1;
                trainer.changeStatus(termLabel.getText().toString(), after);
                feedbackLabel.setText("Status moved (" + before + " -> " + after + ")");
                trainer.save();
            }
        } else {
            feedbackLabel.setText("Can you guess it?");

            redButton.setBackgroundColor(Color.TRANSPARENT);
            greenButton.setBackgroundColor(Color.TRANSPARENT);
            redButton.setEnabled(false);
            greenButton.setEnabled(false);

            Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};

            for (Button button : answerButtons) {
                button.setText(trainer.getTranslation(trainer.randomTermFromDatabase()));
                button.setEnabled(true);
            }
            continueButton.setEnabled(false);

            Random rand = new Random();
            int random = rand.nextInt(4);
            answerButtons[random].setText(trainer.getTranslation(termLabel.getText().toString()));

        }
    }

    public void greenButtonClicked(View view) throws IOException {
        if (feedbackLabel.getText().toString().equals("Did you know the answer? Choose green or red.")) {
            redButton.setBackgroundColor(Color.TRANSPARENT);
            greenButton.setBackgroundColor(Color.TRANSPARENT);
            continueButton.setEnabled(true);
            redButton.setEnabled(false);
            greenButton.setEnabled(false);

            if(Integer.parseInt(trainer.getStatus(termLabel.getText().toString())) == 5) {
                feedbackLabel.setText("Status unchanged (5)");
            } else {
                int before = Integer.parseInt(trainer.getStatus(termLabel.getText().toString()));
                int after = before + 1;
                trainer.changeStatus(termLabel.getText().toString(), after);
                feedbackLabel.setText("Status moved (" + before + " -> " + after + ")");
                trainer.save();
            }
        } else {
            feedbackLabel.setText("Did you know the answer? Choose green or red.");
            Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};
            for (Button button : answerButtons) {
                button.setText(trainer.getTranslation(trainer.randomTermFromDatabase()));
                button.setEnabled(false);
            }
            continueButton.setEnabled(false);

            romanizationLabel.setText(trainer.getRomanization(termLabel.getText().toString()));
            romanizationLabel.setBackgroundResource(0);

            Random rand = new Random();
            int random = rand.nextInt(4);
            answerButtons[random].setText(trainer.getTranslation(termLabel.getText().toString()));

            for (Button button : answerButtons) {
                if (trainer.getTranslation(termLabel.getText().toString()).equals(button.getText().toString()))
                    button.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void romanizationLabelClicked(View view) {
        romanizationLabel.setText(trainer.getRomanization(termLabel.getText().toString()));
        romanizationLabel.setBackgroundResource(0);
    }

    public void answ1ButtonClicked(View view) {
        answButtonClicked(answ1Button);
    }

    public void answ2ButtonClicked(View view) {
        answButtonClicked(answ2Button);
    }

    public void answ3ButtonClicked(View view) {
        answButtonClicked(answ3Button);
    }

    public void answ4ButtonClicked(View view) {
        answButtonClicked(answ4Button);
    }

    public void answButtonClicked(Button setButton) {
        String status = trainer.getStatus(termLabel.getText().toString());
        Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};
        if(trainer.getTranslation(termLabel.getText().toString()).equals(setButton.getText().toString())) {
            setButton.setBackgroundColor(Color.GREEN);
            feedbackLabel.setText("Status unchanged " + "(" + status + ")");

            romanizationLabel.setText(trainer.getRomanization(termLabel.getText().toString()));
            romanizationLabel.setBackgroundResource(0);

        } else {
            setButton.setBackgroundColor(Color.RED);
            for (Button button : answerButtons) {
                if (trainer.getTranslation(termLabel.getText().toString()).equals(button.getText().toString()))
                    button.setBackgroundColor(Color.GREEN);
            }
            feedbackLabel.setText("Status unchanged " + "(" + status + ")");

            romanizationLabel.setText(trainer.getRomanization(termLabel.getText().toString()));
            romanizationLabel.setBackgroundResource(0);
        }
        continueButton.setEnabled(true);
        for(Button button : answerButtons) {
            button.setEnabled(false);
        }
    }
}
