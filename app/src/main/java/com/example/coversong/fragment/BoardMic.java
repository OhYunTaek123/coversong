package com.example.coversong.fragment;

import static android.content.ContentValues.TAG;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.coversong.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 *
 * 홈 화면 탭
 *
 */
public class BoardMic extends Fragment {
    public static final int SINGLE_PERMISSION = 0;
    private int pausePosition = 0;
    private MediaRecorder recorder;

    private EditText uploadName;
    private  ImageView isRecord;
    private boolean isRecording = false;
    private String filePath = null;
    //filePath 에는 파일 저장 경로 입력
    private boolean isPlaying = false; // 재생 중인지 체크하기 위한 변수
    private MediaPlayer mediaPlayer, mediaPlayer2; // MediaPlayer 객체
    private SeekBar seekBar, seekBar2; // SeekBar 객체
    private TextView playTimeTextView, playTimeTextView2; // 현재 재생 시간을 나타내는 TextView 객체
    private TextView totalTimeTextView, totalTimeTextView2; // 총 재생 시간을 나타내는 TextView 객체
    private TextView select_filePath;
    private String selectedFilePath;
    private ImageView playControlImageView, playControlImageView2; // 재생/일시정지를 나타내는 ImageView 객체

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("RecordFile"); //
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri recordUri;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_mic, container, false);

        Button bringMusicBtn = view.findViewById(R.id.bring_music);
        Button startRecordBtn = view.findViewById(R.id.start_record);
        Button btn_bring = view.findViewById(R.id.btn_bring);
        Button btn_recordUpload = view.findViewById(R.id.btn_recordUpload);
        CardView bringMusicCard = view.findViewById(R.id.bring_music_card);
        CardView startRecordCard = view.findViewById(R.id.start_record_card);

        Button btnRecordStart = view.findViewById(R.id.record_start);
        Button btnRecordStop = view.findViewById(R.id.record_stop);
        Button btnUpload = view.findViewById(R.id.btn_upload);

        select_filePath = view.findViewById(R.id.select_filePath);
        seekBar = view.findViewById(R.id.card_seek_bar);
        playTimeTextView = view.findViewById((R.id.card_play_time_text_view));
        totalTimeTextView = view.findViewById((R.id.card_total_time_text_view));
        isRecord = view.findViewById(R.id.is_record_confirm);
        seekBar2 = view.findViewById(R.id.card_seek_bar2);
        playTimeTextView2 = view.findViewById((R.id.card_play_time_text_view2));
        totalTimeTextView2 = view.findViewById((R.id.card_total_time_text_view2));

        playControlImageView = view.findViewById(R.id.card_play_control_image_view);
        playControlImageView2 = view.findViewById(R.id.card_play_control_image_view2);

        bringMusicBtn.setOnClickListener(v -> {
            bringMusicCard.setVisibility(View.VISIBLE);
            startRecordCard.setVisibility(View.GONE);
        });

        startRecordBtn.setOnClickListener(v -> {
            bringMusicCard.setVisibility(View.GONE);
            startRecordCard.setVisibility(View.VISIBLE);
        });
        btn_bring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bring_file();
            }
        });

        btnRecordStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        btnRecordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRecording();
            }
        });
        btn_recordUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRecording_bring();
            }
        });
        playControlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        playControlImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause_bring();
            }
        });
        return view;
    }
    private  void  bring_file(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg"); // 선택 가능한 파일의 유형을 지정하세요
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "파일 선택"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            selectedFilePath = selectedFileUri.getPath();
            if (selectedFilePath != null && selectedFilePath.startsWith("/document/raw:")) {
                selectedFilePath = selectedFilePath.replace("/document/raw:", "");
            }
            select_filePath.setText(selectedFilePath);
        }
    }

    private void startRecording() {

        if (isRecording) {
            return;
        }

        // MediaRecorder 객체 생성
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        isRecord.setVisibility(View.VISIBLE);

        // 녹음 파일 경로 설정
        filePath = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4";
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            Toast.makeText(getActivity(), "녹음을 시작합니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopRecording() {
        if (!isRecording) {
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        Toast.makeText(getActivity(), "녹음이 중지되었습니다.", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), getFilePath() ,Toast.LENGTH_SHORT).show();
        isRecord.setVisibility(View.INVISIBLE);
    }
    private void uploadRecording() {
        if(filePath != null){
            uploadTofirebase(filePath);
        }else{
            Toast.makeText(getActivity(), "녹음을 해주세요", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadRecording_bring() {
        if(selectedFilePath != null){
            uploadTofirebase_bring(selectedFilePath);
        }else{
            Toast.makeText(getActivity(), "파일을 선택해주세요", Toast.LENGTH_SHORT).show();
        }
    }
    //파이어베이스 녹화파일 업로드
    private void uploadTofirebase(String filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        EditText editText = getView().findViewById(R.id.upload_name);
        String fileName = editText.getText().toString() + ".mp3";

        UserApiClient.getInstance().me((user, throwable) -> {
            if(user!=null){
                Uri fileUri = Uri.fromFile(new File(filePath));
                StorageReference storageRef = storage.getReference().child("RecordFiles/"+ user.getId() + "!@" + fileName);
                String userId = String.valueOf(user.getId());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("user").document(userId);
                Map<String, Object> id = new HashMap<>();
                id.put("id", user.getId());
                id.put("video", "RecordFiles/"+ user.getId()+"!@"+fileName);

                assert user.getId() != null;

                docRef.set(id).addOnSuccessListener(documentReference -> Log.d(TAG, "Success add RecordFiles"))
                        .addOnFailureListener(e -> com.google.android.exoplayer2.util.Log.w(TAG, "Error", e));

                UploadTask uploadTask = storageRef.putFile(fileUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    //성공시
                    Toast.makeText(getActivity(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    //실패시
                    Toast.makeText(getActivity(), "업로드 실패", Toast.LENGTH_SHORT).show();
                });
            }else if(throwable != null){

            }
            return null;
        });
    }
    private void uploadTofirebase_bring(String filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        EditText editText = getView().findViewById(R.id.recordUpload_name);
        String fileName = editText.getText().toString() + ".mp3";

        UserApiClient.getInstance().me((user, throwable) -> {
            if(user!=null){
                Uri fileUri = Uri.fromFile(new File(filePath));
                StorageReference storageRef = storage.getReference().child("RecordFiles/"+user.getId() + "!@"+ fileName);
                String userId = String.valueOf(user.getId());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("user").document(userId);
                Map<String, Object> id = new HashMap<>();
                id.put("id", user.getId());
                id.put("video", "RecordFiles/"+ user.getId()+"!@"+fileName);

                assert user.getId() != null;

                docRef.set(id).addOnSuccessListener(documentReference -> Log.d(TAG, "Success add RecordFiles"))
                        .addOnFailureListener(e -> com.google.android.exoplayer2.util.Log.w(TAG, "Error", e));

                UploadTask uploadTask = storageRef.putFile(fileUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    //성공시
                    Toast.makeText(getActivity(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    //실패시
                    Toast.makeText(getActivity(), "업로드 실패", Toast.LENGTH_SHORT).show();
                });
            }else if(throwable != null){

            }
            return null;
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver Cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(Cr.getType(uri));
    }

    private String getFilePath() {
        // 저장소 경로
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRecordings/";

        // 파일 이름 (EditText에 입력된 내용으로부터 가져온다고 가정)
        EditText editText = getView().findViewById(R.id.upload_name);
        String fileName = editText.getText().toString() + ".mp3";

        // 최종적으로 반환할 파일 경로
        return folderPath + fileName;
    }
    private void togglePlayPause() {
        if (mediaPlayer == null) {
            startPlaying();
        } else if (isPlaying) {
            pausePlaying();
        } else {
            resumePlaying();
        }
    }private void togglePlayPause_bring() {
        if (mediaPlayer2 == null) {
            startPlaying_bring();
        } else if (isPlaying) {
            pausePlaying_bring();
        } else {
            resumePlaying_bring();
        }
    }
    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
            isPlaying = true;
            updateSeekBar(seekBar);
        } catch (IOException e) {
            Log.e(TAG, "startPlaying() failed", e);
        }
    }
    private void startPlaying_bring() {
        mediaPlayer2 = new MediaPlayer();
        try {
            mediaPlayer2.setDataSource(selectedFilePath);
            mediaPlayer2.prepare();
            mediaPlayer2.start();

            playControlImageView2.setImageResource(R.drawable.ic_baseline_pause_48);
            isPlaying = true;
            updateSeekBar_bring(seekBar2);
        } catch (IOException e) {
            Log.e(TAG, "startPlaying_bring() failed", e);
        }
    }
    private void pausePlaying() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            isPlaying = false;
        }
    }
    private void pausePlaying_bring() {
        if (mediaPlayer2 != null && isPlaying) {
            mediaPlayer2.pause();
            playControlImageView2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            isPlaying = false;
        }
    }
    private void resumePlaying() {
        mediaPlayer.start();
        playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
        isPlaying = true;
    }
    private void resumePlaying_bring() {
        mediaPlayer2.start();
        playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
        isPlaying = true;
    }
    private void updateSeekBar(SeekBar seekBar) {
        int totalDuration = mediaPlayer.getDuration();
        int currentDuration = mediaPlayer.getCurrentPosition();
        seekBar.setMax(totalDuration);
        seekBar.setProgress(currentDuration);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar(seekBar);
            }
        };
        handler.postDelayed(runnable, 1000); // 1초마다 업데이트

        if (totalDuration == 0) {
            seekBar.setProgress(0);
            playTimeTextView.setText("00:00");
            totalTimeTextView.setText("00:00");
        } else {
            seekBar.setProgress(currentDuration);
            String currentDurationString = convertDuration(currentDuration);
            playTimeTextView.setText(currentDurationString);
            String totalDurationString = convertDuration(totalDuration);
            totalTimeTextView.setText(totalDurationString);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

    }private void updateSeekBar_bring(SeekBar seekBar) {
        int totalDuration = mediaPlayer2.getDuration();
        int currentDuration = mediaPlayer2.getCurrentPosition();
        seekBar.setMax(totalDuration);
        seekBar.setProgress(currentDuration);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar_bring(seekBar);
            }
        };
        handler.postDelayed(runnable, 1000); // 1초마다 업데이트

        if (totalDuration == 0) {
            seekBar.setProgress(0);
            playTimeTextView2.setText("00:00");
            totalTimeTextView2.setText("00:00");
        } else {
            seekBar.setProgress(currentDuration);
            String currentDurationString = convertDuration(currentDuration);
            playTimeTextView2.setText(currentDurationString);
            String totalDurationString = convertDuration(totalDuration);
            totalTimeTextView2.setText(totalDurationString);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer2 != null) {
                    mediaPlayer2.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer2 != null) {
                    mediaPlayer2.pause();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer2 != null) {
                    mediaPlayer2.start();
                }
            }
        });
    }
    private String convertDuration(long duration) {
        long minute = (duration / 1000) / 60;
        long second = (duration / 1000) % 60;

        String min = String.valueOf(minute);
        String sec = String.valueOf(second);

        if (minute < 10) {
            min = "0" + min;
        }
        if (second < 10) {
            sec = "0" + sec;
        }

        return min + ":" + sec;
    }
}