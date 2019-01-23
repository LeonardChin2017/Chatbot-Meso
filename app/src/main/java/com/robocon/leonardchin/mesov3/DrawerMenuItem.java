package com.robocon.leonardchin.mesov3;


import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_PROFILE = 1;
    public static final int DRAWER_MENU_ITEM_REQUESTS = 2;
    public static final int DRAWER_MENU_ITEM_GROUPS = 3;
    public static final int DRAWER_MENU_ITEM_MESSAGE = 4;
    public static final int DRAWER_MENU_ITEM_NOTIFICATIONS = 5;
    public static final int DRAWER_MENU_ITEM_SETTINGS = 6;
    public static final int DRAWER_MENU_ITEM_TERMS = 7;
    public static final int DRAWER_MENU_ITEM_VIDEOCALL = 8;
    public static final int DRAWER_MENU_ITEM_HELP = 9;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 10;

    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText("SAS BI");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_assessment_black_24dp));
                break;
            case DRAWER_MENU_ITEM_REQUESTS:
                itemNameTxt.setText("AR Debugging System");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_build_black_24dp));
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                itemNameTxt.setText("Settings");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
                break;
            case DRAWER_MENU_ITEM_VIDEOCALL:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_phone_black_24dp));
                itemNameTxt.setText("Video Call");
                break;
            case DRAWER_MENU_ITEM_HELP:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_help_black_24dp));
                itemNameTxt.setText("Help");
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_error_black_24dp));
                itemNameTxt.setText("Logout");
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick(){
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                try {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.sas.android.bimobile");
                    if (launchIntent != null) {
                        mContext.startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }catch(Exception e){
                    Toast.makeText(mContext, "Please Install SAS BI!!", Toast.LENGTH_SHORT).show();
                }
                if(mCallBack != null)mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_REQUESTS:
                try {
                    Intent launchIntent2 = mContext.getPackageManager().getLaunchIntentForPackage("com.rd360.android");
                    if (launchIntent2 != null) {
                        mContext.startActivity(launchIntent2);//null pointer check in case package name was not found
                    }
                }catch(Exception e1){
                    Toast.makeText(mContext, "Please Install AR Debugging System!!", Toast.LENGTH_SHORT).show();
                }
                if(mCallBack != null)mCallBack.onRequestMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                Toast.makeText(mContext, "Settings", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onSettingsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_VIDEOCALL:
                try {
                    Intent launchIntent3 = mContext.getPackageManager().getLaunchIntentForPackage("com.example.keng.insightx_videocalling");
                    if (launchIntent3 != null) {
                        mContext.startActivity(launchIntent3);//null pointer check in case package name was not found
                    }
                }catch(Exception e1){
                    Toast.makeText(mContext, "Please Install Video Calling System!!", Toast.LENGTH_SHORT).show();
                }
                if(mCallBack != null)mCallBack.onVideoCallMenuSelected();
                break;
            case DRAWER_MENU_ITEM_HELP:
                Toast.makeText(mContext, "Help", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onHelpMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                Toast.makeText(mContext, "Logout", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onLogoutMenuSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack{
        void onProfileMenuSelected();
        void onRequestMenuSelected();
        void onSettingsMenuSelected();
        void onHelpMenuSelected();
        void onLogoutMenuSelected();
        void onVideoCallMenuSelected();
    }
}