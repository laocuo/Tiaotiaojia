package com.laocuo.jumpjump.chat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.laocuo.jumpjump.JumpApp;
import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.battle.Room;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.utils.L;
import com.laocuo.jumpjump.utils.SnackbarUtil;

public class ChatNewActivity extends AppCompatActivity implements OnClickListener {

    private ListView lv_data;
    private EditText et_content;
    private ProgressDialog mWaittingDialog;
    private MyAdapter myAdapter;
    private List<Chat> messages = new ArrayList<Chat>();
    private BmobRealTimeData data = new BmobRealTimeData();
    private LinearLayout mLayout;
    private String mFirstChatId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        mLayout = (LinearLayout) findViewById(R.id.ll_bottom);
        et_content = (EditText) findViewById(R.id.et_content);
        lv_data = (ListView) findViewById(R.id.lv_data);

        mWaittingDialog = new ProgressDialog(this);
        mWaittingDialog.setCancelable(false);

        myAdapter = new MyAdapter();
        lv_data.setAdapter(myAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        data.unsubTableUpdate("Chat");
        data.unsubRowUpdate("Chat", mFirstChatId);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
        if (user != null) {
            String name = user.getAlias();
            if (TextUtils.isEmpty(name)) {
                name = user.getUsername();
            }
            String content = et_content.getText().toString();
            if (TextUtils.isEmpty(content)) {
                SnackbarUtil.showShortSnackbar(mLayout, "内容不能为空");
                return;
            } else {
                sendMsg(name, content, user.getUsername());
            }
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "请先登录");
        }
    }

    private void init(){
//        Bmob.initialize(this, JumpApp.getContext().getApplicationID());

        BmobQuery bmobQuery = new BmobQuery("Chat");
        L.d("findObjects Chat");
        bmobQuery.findObjects(new FindListener<Chat>() {

            @Override
            public void done(List<Chat> list, BmobException e) {
                if (e == null) {
                    L.d("findObjects Chat done list.size()="+list.size());
                    if (list.size() > 0) {
                        Chat chat = list.get(0);
                        mFirstChatId = chat.getObjectId();
                        L.d("mFirstChatId="+mFirstChatId);
                        data.start(new ValueEventListener() {

                            @Override
                            public void onDataChange(JSONObject arg0) {
                                // TODO Auto-generated method stub
                                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
                                    JSONObject data = arg0.optJSONObject("data");
                                    String name = data.optString("name");
                                    String username = data.optString("username");
                                    String content = data.optString("content");
                                    L.d("UPDATETABLE:name="+name+" content="+content);
                                    messages.add(new Chat(name, content, username));
                                    myAdapter.notifyDataSetChanged();
                                    lv_data.smoothScrollToPosition(myAdapter.getCount() - 1);
                                } else if (BmobRealTimeData.ACTION_UPDATEROW.equals(arg0.optString("action"))) {
                                    JSONObject data = arg0.optJSONObject("data");
                                    String name = data.optString("name");
                                    String username = data.optString("username");
                                    String content = data.optString("content");
                                    L.d("UPDATEROW:name="+name+" content="+content);
                                    messages.add(new Chat(name, content, username));
                                    myAdapter.notifyDataSetChanged();
                                    lv_data.smoothScrollToPosition(myAdapter.getCount() - 1);
                                }
                            }

                            @Override
                            public void onConnectCompleted(Exception e) {
                                // TODO Auto-generated method stub
                                if(data.isConnected()){
//                                    data.subTableUpdate("Chat");
                                    data.subRowUpdate("Chat", mFirstChatId);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void sendMsg(String name, String msg, String username){
//        Chat chat = new Chat(name, msg);
//        chat.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                et_content.setText("");
//                showProgress(false);
//            }
//        });
        if (!TextUtils.isEmpty(mFirstChatId)) {
            showProgress(true);
            Chat chat = new Chat(name, msg, username);
            chat.update(mFirstChatId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        et_content.setText("");
                        showProgress(false);
                    }
                }
            });
        }
    }

    private void showProgress(boolean show) {
        if (show == true) {
            if (mWaittingDialog.isShowing() == false) {
                mWaittingDialog.show();
            }
        } else {
            if (mWaittingDialog.isShowing() == true) {
                mWaittingDialog.dismiss();
            }
        }
    }

    private class MyAdapter extends BaseAdapter{

        ViewHolder holder;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_list_item_recv, null);
                holder = new ViewHolder();

                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            Chat chat = messages.get(position);
            holder.tv_name.setText(chat.getName());
            holder.tv_content.setText(chat.getContent());
//            holder.tv_time.setText(chat.getCreatedAt());

            return convertView;
        }

        class ViewHolder{
            TextView tv_name;
            TextView tv_content;
            TextView tv_time;
        }

    }

}