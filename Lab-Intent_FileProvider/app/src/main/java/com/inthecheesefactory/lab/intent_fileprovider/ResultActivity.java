package com.inthecheesefactory.lab.intent_fileprovider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ResultActivity extends Activity implements View.OnClickListener {
    Button btn_toMain;
    GridView gridView;

    private String[] answer = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        btn_toMain = (Button)findViewById(R.id.tomain);
        btn_toMain.setOnClickListener(this);

        Intent intent = getIntent();
        answer = intent.getStringArrayExtra("answer");

        gridView = (GridView) findViewById(R.id.sudo_matrix);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, answer);

        gridView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
