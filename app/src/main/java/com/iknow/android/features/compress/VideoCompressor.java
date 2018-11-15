package com.iknow.android.features.compress;

import android.content.Context;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.iknow.android.interfaces.VideoCompressListener;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/03/16/3:23 PM
 * version: 1.0
 * description:
 */
public class VideoCompressor {

  //ffmpeg -y -i input.mp4 -strict -2 -vcodec libx264  -preset ultrafast -crf 24 -acodec copy -ar 44100 -ac 2 -b:a 12k -s 640x352 -aspect 16:9 output.mp4
  public static void compress(Context context, String inputFile, String outputFile, final VideoCompressListener callback) {
    String cmd = "-threads 2 -y -i " + inputFile + " -strict -2 -vcodec libx264 -preset ultrafast -crf 35 -acodec copy -ac 2 " + outputFile;// crf (0-51, 51 is the worst)
    String[] command = cmd.split(" ");
    try {
      FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {
        @Override
        public void onFailure(String msg) {
          if (callback != null) {
            callback.onFailure("Compress video failed!");
            callback.onFinish();
          }
        }

        @Override
        public void onSuccess(String msg) {
          if (callback != null) {
            callback.onSuccess("Compress video successed!");
            callback.onFinish();
          }
        }
      });
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
