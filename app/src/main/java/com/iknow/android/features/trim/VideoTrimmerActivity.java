package com.iknow.android.features.trim;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.iknow.android.R;
import com.iknow.android.databinding.ActivityTrimmerLayoutBinding;
import com.iknow.android.interfaces.VideoCompressListener;
import com.iknow.android.interfaces.VideoTrimListener;
import com.iknow.android.features.compress.VideoCompressor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerActivity extends AppCompatActivity implements VideoTrimListener {

  private static final String TAG = "jason";
  private static final String VIDEO_PATH_KEY = "video-file-path";
  public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
  private ActivityTrimmerLayoutBinding mBinding;
  private ProgressDialog mProgressDialog;
  private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

  public static void call(FragmentActivity from, String videoPath) {
    if (!TextUtils.isEmpty(videoPath)) {
      Bundle bundle = new Bundle();
      bundle.putString(VIDEO_PATH_KEY, videoPath);
      Intent intent = new Intent(from, VideoTrimmerActivity.class);
      intent.putExtras(bundle);
      from.startActivityForResult(intent, VIDEO_TRIM_REQUEST_CODE);
    }
  }

  @Override protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trimmer_layout);
    Bundle bd = getIntent().getExtras();
    String path = "";
    if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
    if (mBinding.trimmerView != null) {
      mBinding.trimmerView.setOnTrimVideoListener(this);
      mBinding.trimmerView.initVideoByURI(Uri.parse(path));
    }
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    mBinding.trimmerView.onVideoPause();
    mBinding.trimmerView.setRestoreState(true);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mBinding.trimmerView.onDestroy();
  }

  @Override public void onStartTrim() {
    buildDialog(getResources().getString(R.string.trimming)).show();
  }

  @Override public void onFinishTrim(String in) {
    //TODO: please handle your trimmed video url here!!!
//    String out = "/storage/emulated/0/Android/data/com.iknow.android/cache/compress.mp4";
    String out = getSavePath()+File.separator+new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";
    buildDialog(getResources().getString(R.string.compressing)).show();
    VideoCompressor.compress(this, in, out, new VideoCompressListener() {
      @Override public void onSuccess(String message) {
      }

      @Override public void onFailure(String message) {
      }

      @Override public void onFinish() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        finish();
      }
    });
  }
  private Locale getLocale() {
    Configuration config = getResources().getConfiguration();
    Locale sysLocale = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      sysLocale = getSystemLocale(config);
    } else {
      sysLocale = getSystemLocaleLegacy(config);
    }

    return sysLocale;
  }
  @SuppressWarnings("deprecation")
  public static Locale getSystemLocaleLegacy(Configuration config){
    return config.locale;
  }

  @TargetApi(Build.VERSION_CODES.N)
  public static Locale getSystemLocale(Configuration config){
    return config.getLocales().get(0);
  }

  private String getSavePath(){
    if(null != Environment.getExternalStorageDirectory()) {
      outputDir = Environment.getExternalStorageDirectory().getPath() + "/Compress/";
      File file = new File(outputDir);
      if (!file.exists()) {
        file.mkdirs();
      }
    }else{
      outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }
    return outputDir;
  }

  @Override public void onCancel() {
    mBinding.trimmerView.onDestroy();
    finish();
  }

  private ProgressDialog buildDialog(String msg) {
    if (mProgressDialog == null) {
      mProgressDialog = ProgressDialog.show(this, "", msg);
    }
    mProgressDialog.setMessage(msg);
    return mProgressDialog;
  }
}
