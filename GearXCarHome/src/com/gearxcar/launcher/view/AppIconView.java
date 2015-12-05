package com.gearxcar.launcher.view;

import com.gearxcar.launcher.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AppIconView extends FrameLayout {
    private Boolean mShowText = false;
    private int mTextPos;
    private TextView mAppTitle;
    private ImageView mAppIcon;
    private ImageView mGearXCarIcon;
    public AppIconView(Context context) {
        this(context, null);
    }
    public AppIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.AppIconView, 0, 0);

        try {
            mShowText = a.getBoolean(R.styleable.AppIconView_showText, false);
            mTextPos = a.getInteger(R.styleable.AppIconView_labelPosition, 0);
        } finally {
            a.recycle();
        }
        View view = this.inflate(context, R.layout.icon, null);
        addView(view);
        mAppTitle = (TextView) view.findViewById(R.id.app_title);
        mAppIcon = (ImageView) view.findViewById(R.id.app_icon);
        mGearXCarIcon = (ImageView) view.findViewById(R.id.gearxcar_icon);
    }
    
    public boolean isShowText() {
        return mShowText;
     }

     public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
        requestLayout();
     }
     
     public void setAppTitle(String app_title) {
         mAppTitle.setText(app_title);
     }
     
     public void setAppIcon(Drawable app_icon) {
         mAppIcon.setImageDrawable(app_icon);
     }
     
     public void setGearXCarIcon(boolean visible) {
         if (visible)
             mGearXCarIcon.setVisibility(View.VISIBLE);
         else
             mGearXCarIcon.setVisibility(View.GONE);
     }
}
