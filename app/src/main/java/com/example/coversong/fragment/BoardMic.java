package com.example.coversong.fragment;

import static android.content.ContentValues.TAG;


import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

    private boolean isRecording = false;
    private String filePath = null;
    //filePath 에는 파일 저장 경로 입력
    private boolean isPlaying = false; // 재생 중인지 체크하기 위한 변수
    private MediaPlayer mediaPlayer; // MediaPlayer 객체
    private SeekBar seekBar; // SeekBar 객체
    private TextView playTimeTextView; // 현재 재생 시간을 나타내는 TextView 객체
    private TextView totalTimeTextView; // 총 재생 시간을 나타내는 TextView 객체
    private ImageView playControlImageView; // 재생/일시정지를 나타내는 ImageView 객체

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("RecordFile"); //
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri recordUri;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_mic, container, false);

        Button bringMusicBtn = view.findViewById(R.id.bring_music);
        Button startRecordBtn = view.findViewById(R.id.start_record);
        CardView bringMusicCard = view.findViewById(R.id.bring_music_card);
        CardView startRecordCard = view.findViewById(R.id.start_record_card);

        Button btnRecordStart = view.findViewById(R.id.record_start);
        Button btnRecordStop = view.findViewById(R.id.record_stop);
        Button btnUpload = view.findViewById(R.id.btn_upload);

        seekBar = view.findViewById(R.id.card_seek_bar);
        playTimeTextView = view.findViewById((R.id.card_play_time_text_view));
        totalTimeTextView = view.findViewById((R.id.card_total_time_text_view));

        playControlImageView = view.findViewById(R.id.card_play_control_image_view);



        bringMusicBtn.setOnClickListener(v -> {
            bringMusicCard.setVisibility(View.VISIBLE);
            startRecordCard.setVisibility(View.GONE);
        });

        startRecordBtn.setOnClickListener(v -> {
            bringMusicCard.setVisibility(View.GONE);
            startRecordCard.setVisibility(View.VISIBLE);
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
        playControlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });


        return view;
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
    }
    /*
    private void uploadFile(String uploadUrl, String filePath, String fileName) {
        File file = new File(filePath);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("audio/*"), file))
                .build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // 서버 응답을 처리합니다.
                } else {
                    // 서버 응답이 실패한 경우를 처리합니다.
                }
            }
        });
    } */
    private void uploadRecording() {
        if(filePath != null){

            uploadTofirebase(filePath);
        }else{
            Toast.makeText(getActivity(), "녹음을 해주세요", Toast.LENGTH_SHORT).show();
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
                StorageReference storageRef = storage.getReference().child("RecordFiles/"+ user.getId() + fileName);
                String userId = String.valueOf(user.getId());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("user").document(userId);
                Map<String, Object> id = new HashMap<>();
                id.put("id", user.getId());
                id.put("video", "RecordFiles/"+ user.getId()+fileName);

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
    }
    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
            isPlaying = true;
            Toast.makeText(getActivity(), filePath, Toast.LENGTH_SHORT).show();
            updateSeekBar();
        } catch (IOException e) {
            Log.e(TAG, "startPlaying() failed", e);
        }
    }
    private void pausePlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            pausePosition = mediaPlayer.getCurrentPosition();
            playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            isPlaying = false;
        }
    }
    private void resumePlaying() {
        mediaPlayer.seekTo(pausePosition);
        mediaPlayer.start();
        updateSeekBar();
        playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
    }
    private void updateSeekBar() {
        // 현재 녹음파일의 총 길이를 가져온다.
        int totalDuration = mediaPlayer.getDuration();

        // 현재 녹음파일의 진행 상황을 가져온다.
        int currentDuration = mediaPlayer.getCurrentPosition();

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