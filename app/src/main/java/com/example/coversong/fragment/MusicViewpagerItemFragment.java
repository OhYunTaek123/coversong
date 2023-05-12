package com.example.coversong.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.example.coversong.R;

public class MusicViewpagerItemFragment extends Fragment {
    interface OnContactListener{
        void onContact(MusicViewpagerItemFragment fragment);
        void onChangeColor(Palette.Swatch dominantSwatch);
    }

    View rootView;
    ImageView albumArt_iv;
    Bitmap bitmap;
    CrateBitmapThread crateBitmapThread;
    GeneratedBitmapColorThread generatedBitmapColorThread;
    OnContactListener listener;
    String title;
    int musicRes;
    Palette.Swatch dominantSwatch;

    public MusicViewpagerItemFragment(String title, int musicRes, OnContactListener listener){
        this.title = title;
        this.musicRes = musicRes;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musiclist_item_viewpager, container, false);
        albumArt_iv = findViewById(R.id.albumArt_iv);
        crateBitmapThread = new CrateBitmapThread();
        crateBitmapThread.start();
        return rootView;
    }

    protected <T extends View> T findViewById(int id){return rootView.findViewById(id);}

    @Override
    public void onResume() {
        super.onResume();
        listener.onContact(this);
    }

    class CrateBitmapThread extends Thread{
        CrateBitmapHandler handler = new CrateBitmapHandler();
        @Override
        public void run() {
            super.run();
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.exo_ic_default_album_image);
            Bundle bundle = new Bundle();
            Message message = handler.obtainMessage();
            try{
                Thread.sleep(1);
                message.setData(bundle);
                handler.sendMessage(message);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    class CrateBitmapHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            albumArt_iv.setImageBitmap(bitmap);
            generatedBitmapColorThread = new GeneratedBitmapColorThread();
            generatedBitmapColorThread.start();
        }
    }
    class GeneratedBitmapColorThread extends Thread{
        GeneratedBitmapColorHandler handler = new GeneratedBitmapColorHandler();
        @Override
        public void run() {
            super.run();
            /*
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    dominantSwatch = palette.getDominantSwatch();
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);
                }
            });*/
        }
    }
    class GeneratedBitmapColorHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            listener.onChangeColor(dominantSwatch);
        }
    }
}
