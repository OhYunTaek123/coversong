package com.example.coversong.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import oss.data.BoardItem;
import oss.data.REF;

/**
 * 글쓰기 화면
 *
 */
public class WriteActivity extends AppCompatActivity {

    EditText patientName;
    EditText patientNum;
    EditText hospitalName;
    EditText room;
    Spinner bloodType;

    BoardItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_NeedBlood);
        getSupportActionBar().setTitle("글쓰기");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        /*작성 항목*/
        patientName = findViewById(R.id.write_patient_name);
        patientNum = findViewById(R.id.write_patient_num);
        hospitalName = findViewById(R.id.write_hospital_name);
        room = findViewById(R.id.write_room);


        /*수정 시 원래 값*/
        item = getIntent().getParcelableExtra(REF.LIST.name());
        getData();

        /*혈액형*/
        bloodType = findViewById(R.id.write_bloodType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_type, R.layout.support_simple_spinner_dropdown_item);
        bloodType.setAdapter(adapter);
        bloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.bloodType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*완료*/
        findViewById(R.id.elem_confirm_button).setOnClickListener(v -> {
            confirm();
        });
    }

    private void getData() {
        patientName.setText(item.patient);
        patientNum.setText(item.patientNum);
        hospitalName.setText(item.hospital);
        room.setText(item.room);
    }

    private void setData() {
        item.patient = patientName.getText().toString();
        item.patientNum = patientNum.getText().toString();
        item.hospital = hospitalName.getText().toString();
        Log.d("confirm", "sethospital:"+item.hospital);
        item.room = room.getText().toString();
    }

    private void confirm() {
        Intent intent = new Intent();
        setData();
        intent.putExtra(REF.LIST.name(), item);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        confirm();
        return super.onOptionsItemSelected(item);
    }
}