package com.gearxcar.message.view;

import com.gearxcar.messagesample.R;
import com.gearxcar.messagesample.R.id;
import com.gearxcar.messagesample.R.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ReplyMsgView extends FrameLayout {
    private TextView mReplyMsg;
    public ReplyMsgView(Context context) {
        this(context, null);
    }
    public ReplyMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = this.inflate(context, R.layout.reply, null);
        addView(view);
        mReplyMsg = (TextView) view.findViewById(R.id.reply_msg);
    }
    
    public void setReplyMsg(String reply_msg) {
         mReplyMsg.setText(reply_msg);
     }
}
