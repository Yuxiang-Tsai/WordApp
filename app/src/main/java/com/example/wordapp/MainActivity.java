package com.example.wordapp;

import static java.lang.System.exit;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;


//复制项目 https://www.cnblogs.com/yangfengwu/p/9466929.html  最后还要修改gradle里的设置

public class MainActivity extends AppCompatActivity {

    NavController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onStart() {   //设置左上角 返回键
        super.onStart();
        controller = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
        NavigationUI.setupActionBarWithNavController(this, controller);
    }

    @Override
    public boolean onSupportNavigateUp() {  //实现 左上角 返回键 功能
        controller.navigateUp();
        return super.onSupportNavigateUp();
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //按两次键退出程序
        if (Objects.requireNonNull(controller.getCurrentDestination()).getId() == R.id.wordsFragment) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    //弹出提示，可以有多种方式
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    exit(0);
                }
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}