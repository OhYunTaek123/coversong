package com.example.coversong.fragment;

import static android.content.ContentValues.TAG;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.coversong.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * 홈 화면 탭
 *
 */
public class BoardMic extends Fragment {
    private int pausePosition = 0;
    private MediaRecorder recorder;

    private boolean isRecording = false;
    public String filePath = null;
    //filePath 에는 파일 저장 경로 입력
    private boolean isPlaying = false; // 재생 중인지 체크하기 위한 변수
    private MediaPlayer mediaPlayer; // MediaPlayer 객체
    private SeekBar seekBar; // SeekBar 객체
    private TextView playTimeTextView; // 현재 재생 시간을 나타내는 TextView 객체
    private TextView totalTimeTextView; // 총 재생 시간을 나타내는 TextView 객체
    private ImageView playControlImageView; // 재생/일시정지를 나타내는 ImageView 객체


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_mic, container, false);

        Button bringMusicBtn = view.findViewById(R.id.bring_music);
        Button startRecordBtn = view.findViewById(R.id.start_record);
        CardView bringMusicCard = view.findViewById(R.id.bring_music_card);
        CardView startRecordCard = view.findViewById(R.id.start_record_card);
        seekBar = view.findViewById(R.id.card_seek_bar);
        playTimeTextView = view.findViewById((R.id.card_play_time_text_view));
        totalTimeTextView = view.findViewById((R.id.card_total_time_text_view));

        Button btnRecordStart = view.findViewById(R.id.record_start);
        Button btnRecordStop = view.findViewById(R.id.record_stop);
        Button btnUpload = view.findViewById(R.id.btn_upload);

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


        return view;}
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
    }
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
    }
    private void uploadRecording() {
        EditText uploadName = getView().findViewById(R.id.upload_name);
        String fileName = uploadName.getText().toString() + ".mp3";
        String filePath = getFilePath();
        String uploadUrl = "http://example.com/upload";

        // 파일을 업로드합니다.
        uploadFile(uploadUrl, filePath, fileName);

        // 파일이 업로드되었으므로 로컬 저장소에 저장된 파일을 삭제합니다.
        File file = new File(filePath);
        file.delete();

        // 파일이 삭제된 후에는 녹음 상태를 초기화합니다.
        isRecording = false;
        stopRecording();
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
            String filePath = getFilePath();
            File file = new File(filePath);

            if (!file.exists()) {
                Toast.makeText(getActivity(), "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getActivity(), filePath, Toast.LENGTH_SHORT).show();

            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48);
            isPlaying = true;

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

        // 녹음파일의 총 길이와 현재 진행 상황을 시분초 형태로 변환한다.
        String totalDurationString = convertDuration(totalDuration);
        String currentDurationString = convertDuration(currentDuration);

        seekBar.setMax(totalDuration);

        // 진행바에 현재 진행 상황을 설정한다.
        seekBar.setProgress(currentDuration);

        // 현재 진행 상황을 텍스트뷰에 표시한다.
        playTimeTextView.setText(currentDurationString);

        // 총 길이를 텍스트뷰에 표시한다.
        totalTimeTextView.setText(totalDurationString);
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