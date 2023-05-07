package com.example.coversong.fragment;

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
        allFindViewById();
        return view;
    }

    private void allFindViewById(){
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
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        createMusicViewPager();
    }

    private void createMusicViewPager(){
        musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        if(musicViewPagerAdapter.items.size() == 0){
            musicViewPagerAdapter.items.add(new MusicViewpagerItemFragment("title", R.raw, new MusicViewpagerItemFragment.OnContactListener() {
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
    }
*/
    private void createMediaPlayer(int res){
        mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), res);
        mediaPlayer.seekTo(0);
        mediaPosition = 0;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
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