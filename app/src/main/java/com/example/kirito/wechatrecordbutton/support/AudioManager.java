package com.example.kirito.wechatrecordbutton.support;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.util.UUID;

/**
 * Created by kirito on 2016.11.08.
 */

public class AudioManager {
    private static AudioManager mInstance;
    private MediaRecorder mMediaRecorder;

    private String path;
    private String current_path;
    private audioPrepareListener mListener;
    private boolean isPrepared;

    private static final String TAG = "AudioManager";

    public String getFilePath() {
        return current_path;
    }

    //添加AudioManager prepared完毕之后的回调接口
    public interface audioPrepareListener{
        void audioPrepared();
    }

    public void setAudioPrepareListener(audioPrepareListener listener){
        mListener = listener;
    }

    public AudioManager(String path) {
        this.path = path;
    }

    public static AudioManager getInstance(String path){
        if (mInstance == null){
            synchronized (AudioManager.class){
                mInstance = new AudioManager(path);
            }
        }
        return mInstance;
    }

    public void prepareAudio(){
        isPrepared = false;
        if (mMediaRecorder != null){
            mMediaRecorder.reset();
        }
        try{
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            File mDir = new File(path);
            if (!mDir.exists()){
                mDir.mkdir();
            }
            File file = new File(mDir,getFileName());
            current_path = file.getAbsolutePath();
            mMediaRecorder.setOutputFile(current_path);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            if (mListener != null){
                mListener.audioPrepared();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getVoiceLevel(int maxLevel){
        int level=1;
        if (isPrepared){//mMediaRecorder.getMaxAmplitude()值在1-32768之间
            try{//返回值在1-7之间
                level=maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
                return level;
            }catch (Exception e){
                Log.e("e.toString()" , e.toString());
                e.printStackTrace();
            }
        }


        return level;
    }

    public void releaseAudio(){
        if (mMediaRecorder!= null){
            try{
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void cancelAudio(){
        releaseAudio();
        if (current_path != null){
            File dele_file = new File(current_path);
            dele_file.delete();
            current_path = null;
        }
    }

    private String getFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }
}
