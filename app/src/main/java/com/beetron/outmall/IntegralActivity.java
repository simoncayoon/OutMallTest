package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.PostUser;
import com.beetron.outmall.models.SignInfo;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/13.
 * Time: 16:05.
 */
public class IntegralActivity extends Activity implements CusNaviView.NaviBtnListener {

    private static final String TAG = IntegralActivity.class.getSimpleName();
    private CusNaviView cusNaviView;
    private ListView lvHistory;
    private Button btnExchange;
    private TextView tvScorAmount;

    private List<SignInfo> signDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.integral_detail_layout);
        initView();
        try {
            getScoreInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getScoreInfo() throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_login), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_GET_SCORE_DETAIL;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        postJson.put("uid", TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {
                                try {
                                    JSONObject jsonResult = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                    tvScorAmount.setText(jsonResult.getString("jifen"));
                                    signDetail = gson.fromJson(jsonResult.getString("list"), new TypeToken<List<SignInfo>>(){}.getType());
                                    DebugFlags.logD(TAG, "签到数量:" + signDetail.size());
                                    lvHistory.setAdapter(new SignAdapter());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(IntegralActivity.this, jsonObject.getString(Constants.RESULT_ERROR_FIELD), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initView() {

        signDetail = new ArrayList<SignInfo>();

        initNavi();

        tvScorAmount = (TextView) findViewById(R.id.tv_score_amount);

        lvHistory = (ListView) findViewById(R.id.lv_score_sign_history);

        btnExchange = (Button) findViewById(R.id.btn_score_exchange);
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "正在开发中...");
            }
        });
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
    }

    @Override
    public void leftBtnListener() {
        finish();
    }

    @Override
    public void rightBtnListener() {
        return;
    }

    class SignAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return signDetail.size();
        }

        @Override
        public Object getItem(int position) {
            return signDetail.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.sign_history_item_layout, null);
                viewHolder.signDate = (TextView) convertView.findViewById(R.id.tv_sign_history_item_date);
                viewHolder.score = (TextView) convertView.findViewById(R.id.tv_sign_score);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            SignInfo signInfo = signDetail.get(position);
            viewHolder.signDate.setText(signInfo.getDate());
            viewHolder.score.setText("+ " + signInfo.getJifen());
            return convertView;
        }

        class ViewHolder {
            TextView signDate, score;
        }
    }
}
