package com.beetron.outmall.utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.R;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.UpdateInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by ShelWee on 14-5-8.<br/>
 * Usage:
 * <p/>
 * <pre>
 * UpdateManager updateManager = new UpdateManager.Builder(this)
 * 		.checkUrl(&quot;http://localhost/examples/version.jsp&quot;)
 * 		.isAutoInstall(false)
 * 		.build();
 * updateManager.check();
 * </pre>
 *
 * @author ShelWee(<a href="http://www.shelwee.com">http://www.shelwee.com</a>)
 * @version 0.1 beta
 */
public class UpdateHelper {

    private static final String TAG = UpdateHelper.class.getSimpleName();

    private Context mContext;
    private String checkUrl;
    private boolean isAutoInstall;
    private boolean isHintVersion;
    private OnUpdateListener updateListener;
    private NotificationManager notificationManager;
    private Notification.Builder ntfBuilder;

    private static final int UPDATE_NOTIFICATION_PROGRESS = 0x1;
    private static final int COMPLETE_DOWNLOAD_APK = 0x2;
    private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
    private static final String PATH = Environment
            .getExternalStorageDirectory().getPath();
    private static final String SUFFIX = ".apk";
    private static final String APK_PATH = "APK_PATH";
    private static final String APP_NAME = "APP_NAME";
    private SharedPreferences preferences_update;

    private HashMap<String, String> cache = new HashMap<String, String>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_NOTIFICATION_PROGRESS:
                    showDownloadNotificationUI((UpdateInfo) msg.obj, msg.arg1);
                    break;
                case COMPLETE_DOWNLOAD_APK:
                    if (UpdateHelper.this.isAutoInstall) {
                        installApk(Uri.parse("file://" + cache.get(APK_PATH)));
                    } else {
                        if (ntfBuilder == null) {
                            ntfBuilder = new Notification.Builder(mContext);
                        }
                        ntfBuilder.setSmallIcon(mContext.getApplicationInfo().icon)
                                .setContentTitle(cache.get(APP_NAME))
                                .setContentText("下载完成，点击安装").setTicker("任务下载完成");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(
                                Uri.parse("file://" + cache.get(APK_PATH)),
                                "application/vnd.android.package-archive");
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                mContext, 0, intent, 0);
                        ntfBuilder.setContentIntent(pendingIntent);
                        if (notificationManager == null) {
                            notificationManager = (NotificationManager) mContext
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                        }
                        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                                ntfBuilder.build());
                    }
                    break;
            }
        }

    };

    private UpdateHelper(Builder builder) {
        this.mContext = builder.context;
        this.checkUrl = builder.checkUrl;
        this.isAutoInstall = builder.isAutoInstall;
        this.isHintVersion = builder.isHintNewVersion;
        preferences_update = mContext.getSharedPreferences("Updater",
                Context.MODE_PRIVATE);
    }

    /**
     * 检查app是否有新版本，check之前先Builer所需参数
     */
    public void check() {
        check(null);
    }

    public void check(OnUpdateListener listener) {
        if (listener != null) {
            this.updateListener = listener;
        }
        if (mContext == null) {
            Log.e("NullPointerException", "The context must not be null.");
            return;
        }
        try {
            asyncCheck(checkUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 2014-10-27新增流量提示框，当网络为数据流量方式时，下载就会弹出此对话框提示
     *
     * @param updateInfo
     */
    private void showNetDialog(final UpdateInfo updateInfo) {

        final CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(R.string.prompt_download_title);
        builder.setMessage(R.string.prompt_no_wifi_download);
        builder.setCancelAble(false);
        builder.setPositiveButton(R.string.prompt_continue_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    AsyncDownLoad asyncDownLoad = new AsyncDownLoad();
                    asyncDownLoad.execute(updateInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.prompt_cancel_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 弹出提示更新窗口
     *
     * @param updateInfo
     */
    private void showUpdateUI(final UpdateInfo updateInfo) {

        final CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(updateInfo.getUpdateTips());
        builder.setMessage(updateInfo.getChangeLog());
        builder.setCancelAble(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.prompt_download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetWorkUtils netWorkUtils = new NetWorkUtils(mContext);
                int type = netWorkUtils.getNetType();
                if (type != 1) {
                    showNetDialog(updateInfo);
                } else {
                    AsyncDownLoad asyncDownLoad = new AsyncDownLoad();
                    asyncDownLoad.execute(updateInfo);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.prompt_update_download_next_time), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 通知栏弹出下载提示进度
     *
     * @param updateInfo
     * @param progress
     */
    private void showDownloadNotificationUI(UpdateInfo updateInfo,
                                            final int progress) {
        if (mContext != null) {
            String contentText = new StringBuffer().append(progress)
                    .append("%").toString();
            PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                    0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            if (notificationManager == null) {
                notificationManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (ntfBuilder == null) {
                ntfBuilder = new Notification.Builder(mContext)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setTicker(mContext.getResources().getString(R.string.notification_downloading))
                        .setContentTitle(updateInfo.getAppName())
                        .setContentIntent(contentIntent);
            }
            ntfBuilder.setContentText(contentText);
            ntfBuilder.setProgress(100, progress, false);
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                    ntfBuilder.build());
        }
    }

    /**
     * 获取当前app版本
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private PackageInfo getPackageInfo() {
        PackageInfo pinfo = null;
        if (mContext != null) {
            try {
                pinfo = mContext.getPackageManager().getPackageInfo(
                        mContext.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                DebugFlags.logD(TAG, "PackageInfo " + mContext.getPackageName() + pinfo.versionName + " " + pinfo.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pinfo;
    }

    void asyncCheck(String checkUrl) throws Exception {
        if (UpdateHelper.this.updateListener != null) {
            UpdateHelper.this.updateListener.onStartCheck();
        }
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest checkUpdateReq = new JsonObjectRequest(Request.Method.POST, checkUrl, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            parseUpdate(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });

        NetController.getInstance(mContext.getApplicationContext()).addToRequestQueue(checkUpdateReq, TAG);
    }

    void parseUpdate(JSONObject respon) throws Exception {

        Gson gson = new Gson();
        DebugFlags.logD(TAG, "升级返回内容：" + respon.toString());
        if (respon.getString(Constants.RESPON_STATUS).equals(Constants.FLAG_RESPON_SUCCEED)) {//正常返回
            JSONObject result = new JSONObject(respon.getString(Constants.RESPON_RESULT));
            UpdateInfo updateInfo = gson.fromJson(result.toString(), new TypeToken<UpdateInfo>() {
            }.getType());

            SharedPreferences.Editor editor = preferences_update.edit();
            if (mContext != null && updateInfo != null) {
                DebugFlags.logD(TAG, updateInfo.getAppVersion() + " xxx " + getPackageInfo().versionCode);
                if (Integer.parseInt(updateInfo.getAppVersion()) > getPackageInfo().versionCode) {

                    showUpdateUI(updateInfo);
                    DebugFlags.logD(TAG, "更新APK的下载路径：" + updateInfo.getAppUrl());
                    editor.putBoolean("hasNewVersion", true);
                    editor.putString("lastestVersionCode",
                            updateInfo.getAppVersion());
                    editor.putString("lastestVersionName",
                            updateInfo.getAppVersionName());
                } else {
                    editor.putBoolean("hasNewVersion", false);
                }
            }
            editor.putString("currentVersionCode", getPackageInfo().versionCode
                    + "");
            editor.putString("currentVersionName", getPackageInfo().versionName);
            editor.commit();
            if (UpdateHelper.this.updateListener != null) {
                UpdateHelper.this.updateListener.onFinishCheck(updateInfo);
            }
        } else {
            DebugFlags.logD(TAG, "返回内容是：" + respon.toString());
        }
    }


    /**
     * 异步下载app任务
     */
    private class AsyncDownLoad extends AsyncTask<UpdateInfo, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(UpdateInfo... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://" + params[0].getAppUrl());
            try {
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    return false;
                } else {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    long total = entity.getContentLength();
                    String apkName = params[0].getAppName()
                            + params[0].getAppVersionName() + SUFFIX;
                    cache.put(APP_NAME, params[0].getAppName());
                    cache.put(APK_PATH,
                            PATH + File.separator + params[0].getAppName()
                                    + File.separator + apkName);
                    File savePath = new File(PATH + File.separator
                            + params[0].getAppName());
                    if (!savePath.exists())
                        savePath.mkdirs();
                    File apkFile = new File(savePath, apkName);
                    if (apkFile.exists()) {
                        return true;
                    }
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, length);
                        count += length;
                        int progress = (int) ((count / (float) total) * 100);
                        if (progress % 5 == 0) {
                            handler.obtainMessage(UPDATE_NOTIFICATION_PROGRESS,
                                    progress, -1, params[0]).sendToTarget();
                        }
                        if (UpdateHelper.this.updateListener != null) {
                            UpdateHelper.this.updateListener
                                    .onDownloading(progress);
                        }
                    }
                    inputStream.close();
                    fos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag) {
                handler.obtainMessage(COMPLETE_DOWNLOAD_APK).sendToTarget();
                if (UpdateHelper.this.updateListener != null) {
                    UpdateHelper.this.updateListener.onFinshDownload();
                }
            } else {
                Log.e("Error", "下载失败。");
            }
        }
    }

    public static class Builder {
        private Context context;
        private String checkUrl;
        private boolean isAutoInstall = true;
        private boolean isHintNewVersion = true;

        public Builder(Context ctx) {
            this.context = ctx;
        }

        /**
         * 检查是否有新版本App的URL接口路径
         *
         * @param checkUrl
         * @return
         */
        public Builder checkUrl(String checkUrl) {
            this.checkUrl = checkUrl;
            return this;
        }

        /**
         * 是否需要自动安装, 不设置默认自动安装
         *
         * @param isAuto true下载完成后自动安装，false下载完成后需在通知栏手动点击安装
         * @return
         */
        public Builder isAutoInstall(boolean isAuto) {
            this.isAutoInstall = isAuto;
            return this;
        }

        /**
         * 当没有新版本时，是否Toast提示
         *
         * @param isHint
         * @return true提示，false不提示
         */
        public Builder isHintNewVersion(boolean isHint) {
            this.isHintNewVersion = isHint;
            return this;
        }

        /**
         * 构造UpdateManager对象
         *
         * @return
         */
        public UpdateHelper build() {
            return new UpdateHelper(this);
        }
    }

    private void installApk(Uri data) {
        if (mContext != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(data, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            if (notificationManager != null) {
                notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
            }
        } else {
            Log.e("NullPointerException", "The context must not be null.");
        }

    }

}