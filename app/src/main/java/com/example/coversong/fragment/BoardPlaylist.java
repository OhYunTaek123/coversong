package com.example.coversong.fragment;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coversong.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 홈 플레이리스트
 *
 */
public class BoardPlaylist extends Fragment {

    LinearLayout BoardPlaylist;
    ViewPager2 music_viewPager;
    TextView title_tv, currentTime_tv, durationTime_tv;
    SeekBar mediaPos_sb;
    ImageButton playlist_btn, backwardMusic_btn, playToggle_btn, forward_btn, favorite_btn;
    MusicViewPagerAdapter musicViewPagerAdapter;
    ProgressBar musicListPosition_pb;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false, pauseSeekbarUpdate = false;
    int mediaPosition = 0, mediaDuration = 0, mediaCurrentPosition = 0, currentMusicListPosition = 0;
    UiUpdateThread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_playlist, container, false);

        BoardPlaylist = getView().findViewById(R.id.BoardPlaylist);
        music_viewPager = getView().findViewById(R.id.music_viewPager);
        title_tv = getView().findViewById(R.id.title_tv);
        currentTime_tv = getView().findViewById(R.id.currentTime_tv);
        durationTime_tv = getView().findViewById(R.id.durationTime_tv);
        mediaPos_sb = getView().findViewById(R.id.mediaPos_sb);
        playlist_btn = getView().findViewById(R.id.playlist_btn);
        backwardMusic_btn = getView().findViewById(R.id.backwardMusic_btn);
        playToggle_btn = getView().findViewById(R.id.playToggle_btn);
        forward_btn = getView().findViewById(R.id.forward_btn);
        favorite_btn = getView().findViewById(R.id.favorite_btn);
        musicListPosition_pb = getView().findViewById(R.id.musicListPosition_pb);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        createMusicViewPager();
        backwardMusic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwardMusic();
            }
        });
        playToggle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseMusic();
                }
                else{
                    resumeMusic();
                }
            }
        });
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMusic();
            }
        });
    }

    private void createMusicViewPager(){
        musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        if(musicViewPagerAdapter.items.size() == 0){
            musicViewPagerAdapter.items.add(new MusicViewpagerItemFragment("title", R.drawable.covery_logo_2, new MusicViewpagerItemFragment.OnContactListener() {
                @Override
                public void onContact(MusicViewpagerItemFragment fragment) {
                    title_tv.setText(fragment.title);
                    if(isPlaying){
                        pauseMusic();
                    }
                    createMediaPlayer(fragment.musicRes);
                    resumeMusic();
                    mediaDuration = mediaPlayer.getDuration();
                    setSeekBarValue();
                    uiUpdate();
                }

                @Override
                public void onChangeColor(Palette.Swatch dominantSwatch) {
                    changeUiColor(dominantSwatch);

                }
            }));
        }
        music_viewPager.setAdapter(musicViewPagerAdapter);
        musicListPosition_pb.setMax((musicViewPagerAdapter.getItemCount()-1)*1000);
        music_viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int progress = (int)((position + positionOffset)*1000);
                musicListPosition_pb.setProgress(progress);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentMusicListPosition = position;
            }
        });
    }

    private void createMediaPlayer(int res){
        mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), res);
        mediaPlayer.seekTo(0);
        mediaPosition = 0;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(currentMusicListPosition < musicViewPagerAdapter.getItemCount()-1){
                    music_viewPager.setCurrentItem(currentMusicListPosition +1);
                }else{
                    music_viewPager.setCurrentItem(0);
                }

            }
        });
    }
    // 음악 중지
    private  void pauseMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
            updateButtonImage();
        }
    }
    // 음악 재생
    private void resumeMusic(){
        mediaPlayer.seekTo(mediaPosition);
        mediaPlayer.start();
        isPlaying = true;
        updateButtonImage();
    }
    // 뒤로가기 버튼
    private void backwardMusic(){
        if(mediaCurrentPosition > 1000){
            pauseMusic();
            mediaPosition = 0;
            resumeMusic();
        }else{
            if(currentMusicListPosition > 0){
                music_viewPager.setCurrentItem(currentMusicListPosition - 1);
            }else{
                music_viewPager.setCurrentItem(musicViewPagerAdapter.getItemCount()-1);
            }
        }
    }

    private  void forwardMusic(){
        if(mediaCurrentPosition < mediaDuration - 1000){
            pauseMusic();
            mediaPosition = mediaDuration - 1000;
            resumeMusic();
        }else{
            if(currentMusicListPosition < musicViewPagerAdapter.getItemCount()-1){
                music_viewPager.setCurrentItem(currentMusicListPosition + 1);
            }else{
                music_viewPager.setCurrentItem(0);
            }
        }
    }
    // 재생, 중지버튼 누를시 이모티콘 변하는 메소드
    private  void updateButtonImage(){
        if(isPlaying){
            playToggle_btn.setImageResource(R.drawable.ic_baseline_pause_48);
        }else{
            playToggle_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private  void setSeekBarValue(){
        mediaPos_sb.setMax(mediaDuration);
        mediaPos_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(thread != null){
                    pauseSeekbarUpdate = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(thread != null){
                    pauseSeekbarUpdate = false;
                }
                mediaPlayer.seekTo(mediaPos_sb.getProgress());
            }
        });
    }

    private  void uiUpdate(){
        if(thread != null){
            thread.interrupt();
        }
        thread = new UiUpdateThread();
        thread.start();
    }

    private void changeUiColor(Palette.Swatch paletteSwatch){
        BoardPlaylist.setBackgroundColor(paletteSwatch.getRgb());
        mediaPos_sb.setProgressTintList(ColorStateList.valueOf(paletteSwatch.getRgb()));
        mediaPos_sb.setThumbTintList(ColorStateList.valueOf(paletteSwatch.getRgb()));
        musicListPosition_pb.setProgressTintList(ColorStateList.valueOf(paletteSwatch.getRgb()));
        title_tv.setTextColor(paletteSwatch.getTitleTextColor());
    }
    class MusicViewPagerAdapter extends FragmentStateAdapter{
        ArrayList<Fragment> items = new ArrayList<>();

        public MusicViewPagerAdapter(@NonNull com.example.coversong.fragment.BoardPlaylist fragmentActivity){
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return items.get(position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
    class UiUpdateThread extends Thread{
        UiUpdateHandler handler = new UiUpdateHandler();

        @Override
        public void run() {
            super.run();
            while (true){
                try{
                    if(mediaPlayer != null){
                        mediaCurrentPosition = mediaPlayer.getCurrentPosition();
                        mediaDuration = mediaPlayer.getDuration();
                        String currentTimeLabel = createTimeLabel(mediaCurrentPosition);
                        String durationTimeLabel = createTimeLabel(mediaDuration);
                        Bundle bundle = new Bundle();
                        bundle.putInt("mediaCurrentPosition", mediaCurrentPosition);
                        bundle.putInt("mediaDuration", mediaDuration);
                        bundle.putString("currentTimeLabel", currentTimeLabel);
                        bundle.putString("durationTimeLabel", durationTimeLabel);
                        Message message = handler.obtainMessage();
                        message.setData(bundle);
                        handler.sendMessage(message);
                        bundle = null;
                        try{
                            Thread.sleep(1);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                            break;
                        }
                    }
                }catch (Exception e){

                }
            }
        }

        private String createTimeLabel(int msec){
            String timeLabel;

            int min = msec / 1000 / 60;
            int sec = msec / 1000 % 60;
            timeLabel = min + ":";
            if (sec < 10){
                timeLabel += "0";
            }

            timeLabel += sec;
            return timeLabel;
        }
    }

    class UiUpdateHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            currentTime_tv.setText(msg.getData().getString("currentTimeLabel"));
            durationTime_tv.setText(msg.getData().getString("durationTimeLabel"));
            if(mediaPos_sb.getMax() != mediaDuration){
                mediaPos_sb.setMax(mediaDuration);
            }
            if(!pauseSeekbarUpdate){
                mediaPos_sb.setProgress(msg.getData().getInt("currentTimeLabel"));
            }
        }
    }
}