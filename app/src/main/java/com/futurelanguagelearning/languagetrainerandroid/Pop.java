package com.futurelanguagelearning.languagetrainerandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;

public class Pop extends Activity {
    private static final String TAG = "com.futurelanguagelearning.languagetrainerandroid";
    TrainerFct trainer;
    EditText editTranslation;
    EditText editRomanization;
    EditText editTagList;
    EditText editStatus;
    EditText editSentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height =  dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.62));

        trainer = new TrainerFct();
        trainer.readLWTExportFile();

        Bundle bundle = getIntent().getExtras();

        String term = bundle.getString("currentterm");

        editTranslation = (EditText) findViewById(R.id.editTranslation);
        editRomanization = (EditText) findViewById(R.id.editRomanization);
        editTagList = (EditText) findViewById(R.id.editTagList);
        editStatus = (EditText) findViewById(R.id.editStatus);
        editSentence = (EditText) findViewById(R.id.editSentence);

        editTranslation.setText(trainer.getTranslation(term));
        editRomanization.setText(trainer.getRomanization(term));
        editTagList.setText(trainer.getTagList(term));
        editStatus.setText(trainer.getStatus(term));
        editSentence.setText(trainer.getSentence(term));

    }

    @Override
    protected void onPause() {
        Bundle bundle = getIntent().getExtras();

        String term = bundle.getString("currentterm");

        trainer.changeTranslation(term, editTranslation.getText().toString());
        trainer.changeRomanization(term, editRomanization.getText().toString());
        trainer.changeTagList(term, editTagList.getText().toString());
        trainer.changeStatus(term, Integer.parseInt(editStatus.getText().toString()));
        trainer.changeSentence(term, editSentence.getText().toString());

        try {
            trainer.save();
        } catch (IOException e) {
            Log.v(TAG, e.toString());
        }

        super.onPause();
    }
}
