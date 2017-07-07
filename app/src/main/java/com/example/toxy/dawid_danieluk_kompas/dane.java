package com.example.toxy.dawid_danieluk_kompas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;

public class dane extends AppCompatActivity {

    TextView dane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dane);
        dane = (TextView) findViewById(R.id.dane);

        StringBuilder text = new StringBuilder();
        try {
            InputStream instream = openFileInput("dane.txt");
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line=null;
                while (( line = buffreader.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }}}catch (IOException e) {
            e.printStackTrace();
        }
        dane.setText(text);
    }

    private void odswiez(){
        dane.setText("");
    }

    public void Wyczysc(View view){
        try {
            File file = new File(getFileStreamPath("dane.txt").toString());
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
            odswiez();
            Toast.makeText(this,"DELETED",Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            Toast.makeText(this,"Nie udało się usunąć",Toast.LENGTH_LONG).show();
        }
    }

}

