package com.example.coversong.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import main.WriteActivity;
import oss.data.BoardItem;
import oss.data.REF;
import oss.fragment.HomeFragment;

/**글 내용 화면
 *
 */
public class ElementActivity extends AppCompatActivity {

    private BoardItem boardItem;
    private FirebaseUser user;
    private ActivityResultLauncher<Intent> launcher;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_NeedBlood);
        getSupportActionBar().setTitle("게시물");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        user = FirebaseAuth.getInstance().getCurrentUser();
        boardItem = getIntent().getParcelableExtra(REF.LIST.name());
        myRef = FirebaseDatabase.getInstance().getReference(REF.LIST.name());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        BoardItem boardItem = result.getData().getParcelableExtra(REF.LIST.name());
                        removeData(boardItem.key);
                        boardItem.key = myRef.push().getKey();
                        myRef.child(boardItem.key).setValue(boardItem);
                        HomeFragment.update();
                    }
                    Toast.makeText(this, "수정됨", Toast.LENGTH_SHORT).show();
                    finish();
                });

        TextView patientName = findViewById(R.id.elem_patient_name);
        TextView patientNum =findViewById(R.id.elem_patient_num);
        TextView hospital =findViewById(R.id.elem_hospital);
        TextView room =findViewById(R.id.elem_room);
        TextView bloodType = findViewById(R.id.elem_blood);

        patientName.setText(boardItem.patient);
        patientNum.setText(boardItem.patientNum);
        hospital.setText(boardItem.hospital);
        room.setText(boardItem.room);
        bloodType.setText(boardItem.bloodType);

        findViewById(R.id.elem_confirm_button).setOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_element, menu);
        if (!boardItem.isWriter(user)) {
            menu.findItem(R.id.element_del).setVisible(false);
            menu.findItem(R.id.element_rewrite).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.element_del:
                removeData(boardItem.key);
                Toast.makeText(this, "삭제됨", Toast.LENGTH_SHORT).show();
                HomeFragment.update();
                finish();
                break;
            case R.id.element_rewrite:
                Intent intent = new Intent(this, WriteActivity.class);
                intent.putExtra(REF.LIST.name(), boardItem);
                launcher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeData(String pos) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(REF.LIST.name())
                .child(pos).removeValue();
    }
}