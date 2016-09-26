package com.futurelanguagelearning.languagetrainerandroid;

import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.futurelanguagelearning.languagetrainerandroid";

    private TextView termLabel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trainer = new TrainerFct();
        trainer.readLWTExportFile();

        Log.i(TAG, "onCreate");

        termLabel = (TextView) findViewById(R.id.termLabel);
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

        termLabel.setText("Welcome | Hallo | Привет");
        sentenceLabel.setText("");
        feedbackLabel.setText("Ready when you are :)");
        continueButton.setText("Start");
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            trainer.createImportFiles();
        } catch (IOException e) {
            Log.v(TAG,e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trainer = new TrainerFct();
        trainer.readLWTExportFile();
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

            Random rand = new Random();
            int random = rand.nextInt(4);
            answerButtons[random].setText(trainer.getTranslation(termLabel.getText().toString()));

            for (Button button : answerButtons) {
                if (trainer.getTranslation(termLabel.getText().toString()).equals(button.getText().toString()))
                    button.setBackgroundColor(Color.GREEN);
            }
        }
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
        Button[] answerButtons = {answ1Button, answ2Button, answ3Button, answ4Button};
        if(trainer.getTranslation(termLabel.getText().toString()).equals(setButton.getText().toString())) {
            setButton.setBackgroundColor(Color.GREEN);
            feedbackLabel.setText("Good job!");
        } else {
            setButton.setBackgroundColor(Color.RED);
            for (Button button : answerButtons) {
                if (trainer.getTranslation(termLabel.getText().toString()).equals(button.getText().toString()))
                    button.setBackgroundColor(Color.GREEN);
            }
            feedbackLabel.setText("Maybe next time..");
        }
        continueButton.setEnabled(true);
        for(Button button : answerButtons) {
            button.setEnabled(false);
        }
    }
}
