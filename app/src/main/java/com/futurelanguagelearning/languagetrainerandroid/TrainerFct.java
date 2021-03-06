package com.futurelanguagelearning.languagetrainerandroid;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TrainerFct {

    private static final String TAG = "com.futurelanguagelearning.languagetrainerandroid";
    HashMap<String, ArrayList<String>> database = new HashMap<>();

    public HashMap<String, ArrayList<String>> getDatabase() {
        return database;
    }

    public void readLWTExportFile() {
        String path = "/storage/emulated/0/LWTexport";
//        File sdcard = Environment.getExternalStorageDirectory(); //this would be used for a public app coz phones use different paths
        File file = new File(path,"lwt.txt");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String line;
            String[] col;

            while ((line = br.readLine()) != null) {
                col = line.split("\t");

                ArrayList<String> meta = new ArrayList<>();

                meta.add(col[1]); // translation
                meta.add(col[2]); // sentence
                meta.add(col[3]); // romanization
                meta.add(col[4]); // status
                meta.add(col[5]); // language
                if(col.length > 6 && col[6] != null) { //id
                    meta.add(col[6]); // id
                } else {
                    meta.add("");
                }
                if(col.length > 7 && col[7] != null) { //tag list
                    meta.add(col[7]);
                } else {
                    meta.add("");
                }
                database.put(col[0], meta);
            }
            br.close();
        }
        catch (IOException e) {
            Log.v(TAG, e.toString());
        }
        Log.v(TAG,"Database loaded...");
    }

    /**
     * Saves a list to the WordDataBase file in the correct LWT export format
     * @throws IOException
     */
    public void save() throws IOException {
        String path = "/storage/emulated/0/LWTexport";
//        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(path,"lwt.txt");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));

        for(Map.Entry<String, ArrayList<String>> set : database.entrySet()) {
            String output = set.getKey()+listToString(database.get(set.getKey()));

            bw.write(output);
            bw.newLine();
        }
        bw.close();
        Log.v(TAG,"Database updated...");
    }

    /**
     * Getter
     * @param term
     * @return translation of term
     */
    public String getTranslation(String term) {
        return database.get(term).get(0);
    }

    /**
     * Getter
     * @param term
     * @return romanization of term
     */
    public String getRomanization(String term) {
        return database.get(term).get(2);
    }

    /**
     * Getter
     * @param term
     * @return romanization of term
     */
    public String getTagList(String term) {
        return database.get(term).get(6);
    }

    /**
     * Getter
     * @param term
     * @return status of term (int!)
     */
    public String getStatus(String term) {
        return database.get(term).get(3);
    }

    /**
     * Getter
     * @param term
     * @return sentence of term
     */
    public String getSentence(String term) {
        return database.get(term).get(1);
    }

    /**
     * Change the translation of a word
     * @param word - of which the translation needs to be changed
     * @param newTranslation - * for empty (only allowed for statuses 98 and 99)
     */
    public void changeTranslation(String word, String newTranslation) {
        database.get(word).set(0, newTranslation);
    }

    /**
     * Change the romanization of a word
     * @param word - of which the romanization needs to be changed
     * @param newRomanization - new romanized version of a word (Особенно - asobena)
     */
    public void changeRomanization(String word, String newRomanization) {
        database.get(word).set(2, newRomanization);
    }

    /**
     * Change the taglist of a word
     * @param word - of which the taglist needs to be changed
     * @param newTagList - new name for a tag list (example: adjective, adverb, noun)
     */
    public void changeTagList(String word, String newTagList) {
        database.get(word).set(6, newTagList);
    }


    /**
     * Change the status of a word
     * @param word - of which the status needs to be changed
     * @param newStatus - level 1-5 | 98 for ignore and 99 for WellKnown
     */
    public int changeStatus(String word, int newStatus) {
        if(newStatus < 1) {
            database.get(word).set(3, Integer.toString(1));
        } else if(newStatus > 5 && newStatus < 98) {
            database.get(word).set(3, Integer.toString(5));
        } else {
            database.get(word).set(3, Integer.toString(newStatus));
        }
        return Integer.parseInt(database.get(word).get(3));
    }

    /**
     * Change the example sentence of a word
     * @param word - of which the sentence needs to be changed
     * @param newSentence - String should contain word in {} (example: Особенно он {любит} мёд.)
     */
    public void changeSentence(String word, String newSentence) {
        database.get(word).set(1, newSentence);
    }

    /**
     * Exports everything to the LWTimport folder (Creates a textfile for each status
     * -> can be imported via LWT - Import Term (in this version each still needs to be imported manually)
     * @throws IOException
     */
    public void createImportFiles() throws IOException {
        File dir = new File("/storage/emulated/0/LWTimport");
//        File dir = new File("/storage/emulated/0/Android/data/com.dropbox.android/files/u357296010/scratch/android/language-trainer-android/LWTimport");
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                child.delete(); //deletes already existent files before writing new ones
            }
        } else {
            Log.v(TAG,"Directory not existent.. (createImportFiles)");
        }

        HashMap<String,ArrayList<String>> temp = new HashMap<>();
        temp.putAll(database);

        BufferedWriter bw = null;

        for(Map.Entry<String, ArrayList<String>> set : temp.entrySet()) {
            int status = Integer.parseInt(set.getValue().get(3));
            String filename = "/storage/emulated/0/LWTimport/WordDatabase_Status";
//            String filename = "/storage/emulated/0/Android/data/com.dropbox.android/files/u357296010/scratch/android/language-trainer-android/LWTimport/WordDatabase_Status";

            set.getValue().add(3, set.getValue().get(6));

            String output = set.getKey()+listToString(temp.get(set.getKey()));

            switch (status) {
                case 1:		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 2:		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 3:		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 4:		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 5:		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 98:	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+"_Ignore"+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                case 99:	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+status+"_WKn"+".txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
                default: 	bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("LWTimport/Corrupt-Status.txt",true),"UTF-8"));
                    bw.write(output);
                    bw.newLine();
                    bw.flush();
                    break;
            }
        }
        bw.close();

        File directory = new File("/storage/emulated/0/LWTimport");
        File dropbox = new File("/storage/emulated/0/Android/data/com.dropbox.android/files/u357296010/scratch/android/language-trainer-android/LWTimport");

        try {
            FileUtils.copyDirectory(directory, dropbox);
        } catch (IOException e) {
            Log.v(TAG, e.toString());
        }

        Log.v(TAG,"Import files for LWT successfully written...");
    }

    public static String listToString(List<?> list) {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            result += "\t" + list.get(i);
        }
        return result;
    }

    public String randomTermFromDatabase() {
        String randomKey = "";
        Boolean bool = true;

        while(true) {
            List<String> keys = new ArrayList<>(database.keySet());
            Random random = new Random();
            randomKey = keys.get(random.nextInt(keys.size()));
            if ((Integer.parseInt(database.get(randomKey).get(3)) != 98) && (Integer.parseInt(database.get(randomKey).get(3)) != 99)
                    && !(database.get(randomKey).get(0).replace("\\s+", "").equals("*")) && !(database.get(randomKey).get(0).replace("\\s+", "").equals(""))   ) {
                bool = false;
                return randomKey;
            } else {
                bool = true;
            }
        }
    }

}
