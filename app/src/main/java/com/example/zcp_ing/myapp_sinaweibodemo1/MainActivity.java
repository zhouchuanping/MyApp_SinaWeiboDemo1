
package com.example.zcp_ing.myapp_sinaweibodemo1;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import fragment.FragmentController;
import utils.ToastUtils;


public class MainActivity extends FragmentActivity implements OnCheckedChangeListener,OnClickListener{

    private RadioGroup rg_tab;
    private ImageView iv_add;
    private FragmentController fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        /**
         * 横竖屏设置
         *  横屏：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         *  竖屏：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         *  全屏：getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         *
         *  去掉标题栏：
         *  requestWindowFeature(Window.FEATURE_NO_TITLE);
         */
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();

        fc = FragmentController.getInstance(this, R.id.fl_content);
        fc.showFragment(0);

    }

    private void initView() {
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        rg_tab.setOnCheckedChangeListener(this);
        iv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteStatusActivity.class);
                startActivityForResult(intent, 110);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkId) {
        switch (checkId) {
            case R.id.rb_home:
                fc.showFragment(0);
                break;
            case R.id.rb_shop:
                fc.showFragment(1);
                break;
            case R.id.rb_search:
                fc.showFragment(2);
                break;
            case R.id.rb_user:
                fc.showFragment(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                ToastUtils.showToast(this,"add", Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }

    //
//	@Override
//	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//		super.onActivityResult(arg0, arg1, arg2);
//
//		Fragment fragment = fc.getFragment(0);
//		if(fragment instanceof HomeFragment) {
//			HomeFragment homeFragment = (HomeFragment) fragment;
//			homeFragment.initData();
//		}
//	}
}
