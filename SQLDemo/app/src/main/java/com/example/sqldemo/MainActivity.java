package com.example.sqldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.sqldemo.bean.User;
import com.example.sqldemo.db.BaseDAO;
import com.example.sqldemo.db.DAOFactory;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //插入数据
    public void onInsert(View view){
       BaseDAO baseDAO =  DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(User.class);
       baseDAO.insert(new User(1, "lixue", 2, "北京市顺义区胜利街道义宾北区"));
    }
}
