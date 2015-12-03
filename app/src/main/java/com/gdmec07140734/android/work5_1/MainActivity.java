package com.gdmec07140734.android.work5_1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;


import static android.media.MediaPlayer.*;

public class MainActivity extends Activity {
    private Display currDisplay;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private int vWidth, vHeight;
    private Timer timer;
    private ImageButton rew;
    private ImageButton pause;
    private ImageButton start;
    private ImageButton ff;
    private TextView play_time;
    private TextView all_time;
    private TextView title;
    private SeekBar seekbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String mPath = "";
        if (uri != null) {
            mPath = uri.getPath();
        } else {
            Bundle localBundle = getIntent().getExtras();
            if (localBundle != null) {
                String t_path = localBundle.getString("path");
                if (t_path != null && !"".equals(t_path)) {
                    mPath = t_path;

                }
            }
        }
        title = (TextView) findViewById(R.id.title);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        rew = (ImageButton) findViewById(R.id.rew);
        pause = (ImageButton) findViewById(R.id.pause);
        start = (ImageButton) findViewById(R.id.start);
        ff = (ImageButton) findViewById(R.id.ff);

        play_time = (TextView) findViewById(R.id.play_time);
        all_time = (TextView) findViewById(R.id.all_time);
        seekbar = (SeekBar) findViewById(R.id.seekbar);

        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
                player.prepareAsync();

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        player = new MediaPlayer();
        player.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                vWidth = player.getVideoWidth();
                vHeight = player.getVideoHeight();

                if (vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight()) {
                    float wRatio = (float) vWidth / (float) currDisplay.getWidth();
                    float hRatio = (float) vHeight / (float) currDisplay.getHeight();

                    float ratio = Math.max(wRatio, hRatio);
                    vWidth = (int) Math.ceil((float) vWidth / ratio);
                    vHeight = (int) Math.ceil((float) vHeight / ratio);
                    surfaceView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));
                    player.start();
                } else {
                    player.start();

                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                timer = new Timer();
                timer.schedule(new MyTask(), 50, 500);
            }
        });

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            if (!mPath.equals("")) {
                title.setText(mPath.substring(mPath.lastIndexOf("/") + 1));
                player.setDataSource(mPath);
                player.setDataSource(mPath);
            } else {

                AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.raw.exodus);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pause.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                player.pause();
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                timer = new Timer();
                timer.schedule(new MyTask(), 50, 500);

            }
        });

        rew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (player.isPlaying()) {
                    int currentPosition = player.getCurrentPosition();
                    if (currentPosition - 10000 > 0) {
                        player.seekTo(currentPosition - 10000);
                    }
                }
            }
        });


        ff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (player.isPlaying()) {

                    int currentPosition = player.getCurrentPosition();
                    if (currentPosition + 10000 < player.getDuration()) {
                        player.seekTo(currentPosition + 10000);
                    }
                }
            }
        });

        currDisplay = this.getWindowManager().getDefaultDisplay();
    }

    public boolean onCreaqteOptionMenu(Menu menu) {

        menu.add(0, 1, 0, "文件夹");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public class MyTask extends TimerTask {

        public void run() {

            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

        }
    }

    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Time progress = new Time(player.getCurrentPosition());
                    Time allTime = new Time(player.getDuration());
                    String timeStr = progress.toString();
                    Log.v("progress", timeStr);
                    String timeStr2 = allTime.toString();

                    play_time.setText(timeStr.substring(timeStr.indexOf(":") + 1));
                    all_time.setText(timeStr2.substring(timeStr.indexOf(":") + 1));
                    int progressValue = 0;
                    if (player.getDuration() > 0) {
                        progressValue = seekbar.getMax() * player.getCurrentPosition() / player.getDuration();
                    }
                    seekbar.setProgress(progressValue);
                    break;
            }
            super.handleMessage(msg);

        }

    };
}
