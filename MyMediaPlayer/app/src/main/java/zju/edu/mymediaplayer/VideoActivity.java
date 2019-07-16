package zju.edu.mymediaplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Locale;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnSetting;
    Button btnPlay;
    Button btnStop;
    TextView tvTime;
    SeekBar seekBar;
    SeekBar voiceBar;
    VideoView videoView;
    LinearLayout bottomCtr;
    RelativeLayout rlPlayer;
    RelativeLayout rlLoading;
    private AudioManager audioManager;
    private boolean menu_visible = true;
    private boolean isPortrait = true;
    int mVideoWidth = 0;
    int mVideoHeight = 0;
    private Handler handler;
    public static final int MSG_REFRESH = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        btnPlay =(Button) findViewById(R.id.btn_play);
        btnStop =(Button) findViewById(R.id.btn_stop);
        btnSetting =(Button) findViewById(R.id.btn_setting);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        voiceBar = (SeekBar) findViewById(R.id.voiceBar);
        videoView = (VideoView) findViewById(R.id.video_view);
        bottomCtr = (LinearLayout) findViewById(R.id.include_play_bottom) ;
        rlPlayer = (RelativeLayout) findViewById(R.id.rl_player);
        rlLoading = findViewById(R.id.rl_loading);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        voiceBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        voiceBar.setOnSeekBarChangeListener(new AudioVolumeChangeEvent());

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!menu_visible) {
                    bottomCtr.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_bottom);
                    bottomCtr.startAnimation(animation);
                    menu_visible = true;
                } else {
                    bottomCtr.setVisibility(View.INVISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_bottom);
                    bottomCtr.startAnimation(animation);
                    menu_visible = false;
                }
                return false;
            }
        });

        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.big_buck_bunny);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 50);
                mVideoWidth = mediaPlayer.getVideoWidth();
                mVideoHeight = mediaPlayer.getVideoHeight();
                videoScreenInit();
                videoView.start();
                rlLoading.setVisibility(View.GONE);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //进度改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
                handler.removeCallbacksAndMessages(null);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动
                videoView.seekTo(videoView.getDuration() * seekBar.getProgress() / 100);
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 100);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REFRESH:
                        if (videoView.isPlaying()) {
                            refresh();
                            handler.sendEmptyMessageDelayed(MSG_REFRESH, 50);
                        }

                        break;
                }

            }
        };

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekBar.setProgress(100);
                voiceBar.setProgress(100);
                btnPlay.setText("播放");
                btnStop.setText("播放");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                PlayVideo();
                break;
            case R.id.btn_stop:
                StopVideo();
                break;
            case R.id.btn_setting:
                toggle();
                break;
            default:
                break;
        }
    }

    public void PlayVideo(){
        String play = btnPlay.getText().toString();
        if(play.equals("播放")){
            videoView.start();
            btnPlay.setText("暂停");
            btnStop.setText("停止");
            handler.sendEmptyMessageDelayed(MSG_REFRESH, 50);
        }else if(play.equals("暂停")){
            videoView.pause();
            btnPlay.setText("播放");
        }
    }
    public void StopVideo(){
        String play = btnStop.getText().toString();
        if(play.equals("播放")){
            videoView.resume();
            videoView.start();
            btnStop.setText("停止");
            btnPlay.setText("暂停");
            btnPlay.setEnabled(true);
        }else if(play.equals("停止")){
            //videoView.pause();
            videoView.stopPlayback();
            btnStop.setText("播放");
            btnPlay.setEnabled(false);
        }
    }

    private void refresh() {
        long current = videoView.getCurrentPosition() / 1000;
        long duration = videoView.getDuration() / 1000;
        long current_second = current % 60;
        long current_minute = current / 60;
        long total_second = duration % 60;
        long total_minute = duration / 60;
        String time = current_minute + ":" + current_second + "/" + total_minute + ":" + total_second;
        tvTime.setText(time);
        if (duration != 0) {
            seekBar.setProgress((int) (current * 100 / duration));
        }
    }

    private void videoScreenInit() {
        if (isPortrait) {
            portrait();
        } else {
            lanscape();
        }
    }

    private void toggle() {
        if (!isPortrait) {
            portrait();
        } else {
            lanscape();
        }
    }

    private void portrait() {
        videoView.pause();
        isPortrait = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        float width = wm.getDefaultDisplay().getWidth();
        float height = wm.getDefaultDisplay().getHeight();
        float ratio = width / height;
        if (width < height) {
            ratio = height/width;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlPlayer.getLayoutParams();
        layoutParams.height = (int) (mVideoHeight * ratio);
        layoutParams.width = (int) width;
        rlPlayer.setLayoutParams(layoutParams);
        btnSetting.setText("全屏");
        videoView.start();
    }

    private void lanscape() {
        videoView.pause();
        isPortrait = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        float width = wm.getDefaultDisplay().getWidth();
        float height = wm.getDefaultDisplay().getHeight();
        float ratio = width / height;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlPlayer.getLayoutParams();

        layoutParams.height = (int) RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = (int) RelativeLayout.LayoutParams.MATCH_PARENT;
        rlPlayer.setLayoutParams(layoutParams);
        btnSetting.setText("小屏");
        videoView.start();
    }

    public class AudioVolumeChangeEvent implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){
        }
    }
}
