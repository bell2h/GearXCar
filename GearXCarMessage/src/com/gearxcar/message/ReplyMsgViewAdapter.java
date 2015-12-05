package com.gearxcar.message;

import java.util.List;

import com.gearxcar.message.view.ReplyMsgView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ReplyMsgViewAdapter extends BaseAdapter {
    List<String> replyMsgList;

    ReplyMsgViewAdapter(List<String> replyMsgList) {
        super();
        this.replyMsgList = replyMsgList;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return replyMsgList.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return replyMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ReplyMsgView view;
        Context context = parent.getContext();
        if (convertView == null) {
            view = new ReplyMsgView(context);
        } else {
            view = (ReplyMsgView) convertView;
        }
        String replyMsg = replyMsgList.get(position);
        view.setReplyMsg(replyMsg);
        return view;
    }

}
