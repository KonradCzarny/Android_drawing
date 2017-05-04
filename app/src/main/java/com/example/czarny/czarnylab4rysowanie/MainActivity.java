package com.example.czarny.czarnylab4rysowanie;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private PowierzchniaRysunku rysunek = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rysunek = (PowierzchniaRysunku) findViewById(R.id.powierzchnia_rysunku);

        //Wybieranie koloru
        findViewById(R.id.czerwony).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rysunek.wybierzKolor(Color.RED);
            }
        });

        findViewById(R.id.zolty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rysunek.wybierzKolor(Color.YELLOW);
            }
        });

        findViewById(R.id.niebieski).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rysunek.wybierzKolor(Color.BLUE);
            }
        });

        findViewById(R.id.zielony).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rysunek.wybierzKolor(Color.GREEN);
            }
        });

        findViewById(R.id.czysc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rysunek.czysc();
            }
        });
    }
}
