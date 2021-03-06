/**
 * @file VideoPlayActivity.java
 * @brief This file implement video list and video player
 *
 * @author xiaohua.lu
 * @email luxiaohua@agora.io
 * @version 1.0.0.1
 * @date 2021-11-17
 * @license Copyright (C) 2021 AgoraIO Inc. All rights reserved.
 */

package com.agora.agoracalldemo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agora.agoracallkit.callkit.AgoraCallKit;
import com.agora.agoracallkit.callkit.CallKitAccount;
import com.agora.agoracallkit.callkit.ICallKitCallback;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.databinding.ActivityVideoPlayBinding;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayActivity extends AppCompatActivity implements ICallKitCallback,
        VideoListAdapter.IAdapterCallback {
    private final String TAG = "DEMO/VideoActivity";
    public static String DEVICE_ID_KEY = "DEVICE_ID";


    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// Variable Definition /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private VideoPlayActivity mActivity;
    private ActivityVideoPlayBinding mBinding;
    private OrientationUtils orientationUtils;
    private String mDeviceId;
    private GSYVideoOptionBuilder mGsyVideoOption;
    private boolean isPause;
    private boolean isPlay;



    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Override Activity Methods ////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mBinding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mDeviceId = getIntent().getExtras().getString(DEVICE_ID_KEY);

        // ????????????????????????
        List<AgoraCallKit.AlarmRecord> recordList = AgoraCallKit.getInstance().queryAlarmByDeviceId(mDeviceId);

        // ????????????????????????
        mBinding.rvVideoList.setAdapter(new VideoListAdapter(recordList, this));
        mBinding.rvVideoList.setLayoutManager(new LinearLayoutManager(this));

        mBinding.btRtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnTalking();
            }
        });

        initPlayer();
    }

    private void initPlayer() {
        //????????????????????????????????????
        orientationUtils = new OrientationUtils(this, mBinding.player);
        //?????????????????????????????????
        orientationUtils.setEnable(false);

//        mGsyVideoOption= new GSYVideoOptionBuilder();
//        mGsyVideoOption.setThumbImageView(null)
//                .setIsTouchWiget(true)
//                .setRotateViewAuto(false)
//                .setLockLand(false)
//                .setAutoFullWithSize(true)
//                .setShowFullAnimation(false)
//                .setNeedLockFull(true)
//                //.setUrl("https://media.w3.org/2010/05/sintel/trailer.mp4")
//                .setUrl("http://agora-iot-oss-test.oss-cn-shanghai.aliyuncs.com/18e12695164239fc4655aa84d58ec9ba_96_1.m3u8")
//                .setCacheWithPlay(false)
//                .setVideoTitle("????????????")
//                .setVideoAllCallBack(new GSYSampleCallBack() {
//                    @Override
//                    public void onPrepared(String url, Object... objects) {
//                        super.onPrepared(url, objects);
//                        //????????????????????????????????????
//                        orientationUtils.setEnable(true);
////                        isPlay = true;
//                    }
//
//                    @Override
//                    public void onQuitFullscreen(String url, Object... objects) {
//                        super.onQuitFullscreen(url, objects);
//                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
//                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//???????????????player
//                        if (orientationUtils != null) {
//                            orientationUtils.backToProtVideo();
//                        }
//                    }
//                }).setLockClickListener(new LockClickListener() {
//            @Override
//            public void onClick(View view, boolean lock) {
//                if (orientationUtils != null) {
//                    //???????????????onConfigurationChanged
//                    orientationUtils.setEnable(!lock);
//                }
//            }
//        }).build(mBinding.player);

        mBinding.player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                orientationUtils.resolveByClick();

                //?????????true??????????????????actionbar????????????true??????????????????statusbar
                mBinding.player.startWindowFullscreen(VideoPlayActivity.this, true, true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        mBinding.player.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        mBinding.player.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            mBinding.player.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "<onStart>");
        super.onStart();

        //??????????????????
        AgoraCallKit.getInstance().registerListener(this);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "<onStop>");
        super.onStop();

        // ??????????????????
        AgoraCallKit.getInstance().unregisterListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //????????????????????????
        if (isPlay && !isPause) {
            mBinding.player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    /*
     * @brief ??????????????????
     */
    void onBtnTalking() {
//        String tips = "??????????????????: " + mDeviceId + " .....";
//        mPgDigLogShow(tips);
//
//        CallKitAccount dialAccount = new CallKitAccount(mDeviceId, CallKitAccount.ACCOUNT_TYPE_DEV);
//        List<CallKitAccount> dialList = new ArrayList<>();
//        dialList.add(dialAccount);
//        String attachMsg = "I'm doorbell app";
//        int ret = AgoraCallKit.getInstance().callDial(dialList, attachMsg);
//        if (ret != AgoraCallKit.ERR_NONE) {
//            mPgDigLogHide();
//            mPopupMessage("????????????, ?????????: " + ret);
//            return;
//        }

//        // ?????????????????????????????????????????????????????????
//        Intent intent = new Intent( VideoPlayActivity.this, RtcActivity.class);
//        intent.putExtra(RtcActivity.DEVICE_ID_KEY, mDeviceId);
//        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////
    /////////////////// Override ICallKitCallback Methods //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoginOtherDevice(CallKitAccount account) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupMessage(getString(R.string.ACCOUNT_REMOTE_LOGIN));

                // ?????????????????????????????????????????????????????????Activity??????
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(VideoPlayActivity.this, LoggedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        },
                        3000);
            }
        });
    }

    @Override
    public void onPeerIncoming(CallKitAccount account, CallKitAccount peer_account, String attachMsg) {
        Log.d(TAG, "<onPeerIncoming>");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent activityIntent = new Intent(VideoPlayActivity.this, CalledActivity.class);
                activityIntent.putExtra("caller_name", peer_account.getName());
                activityIntent.putExtra("attach_msg", attachMsg);
                startActivity(activityIntent);
            }
        });
    }

    @Override
    public void onAlarmReceived(CallKitAccount account, CallKitAccount peer_account,
                                long timestamp, String alarmMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupMessage(getString(R.string.RECEIVE_FROM) + " " + peer_account.getName() + " " + getString(R.string.ALARM_MESSAGE) + alarmMsg);

                if (mDeviceId.equals(peer_account.getName())) {
                    // ??????????????????????????????
                    List<AgoraCallKit.AlarmRecord> recordList = AgoraCallKit.getInstance().queryAlarmByDeviceId(mDeviceId);
                    mBinding.rvVideoList.setAdapter(new VideoListAdapter(recordList, mActivity));
                    mBinding.rvVideoList.invalidate();
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////
    /////////////////// Override IAdapterCallback Methods //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onItemClicked(int position, AgoraCallKit.AlarmRecord item)   {

        mGsyVideoOption= new GSYVideoOptionBuilder();
        mGsyVideoOption.setThumbImageView(null)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(item.mVideoUrl)
                .setCacheWithPlay(false)
                .setVideoTitle(item.mMessage)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //????????????????????????????????????
                        orientationUtils.setEnable(true);
//                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//???????????????player
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //???????????????onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(mBinding.player);
    }


    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////// Internal Methods /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    void popupMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    void popupMessageLongTime(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}