package com.example.sqldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sqldemo.bean.Product;
import com.example.sqldemo.bean.User;
import com.example.sqldemo.db.BaseDAO;
import com.example.sqldemo.db.BaseDAOImp;
import com.example.sqldemo.db.DAOFactory;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //插入数据
    public void onInsert(View view){
       BaseDAO baseDAO =  DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(User.class, BaseDAO.class);
       baseDAO.insert(new User(1, "lixue", 2, "北京市顺义区胜利街道义宾北区"));
       baseDAO.insert(new User(2, "lili", 1, "北京市顺义区胜利街道义宾北区"));
       baseDAO.insert(new User(3, "mark", 2, "北京市顺义区胜利街道义宾北区"));
       baseDAO.insert(new User(4, "toney", 1, "北京市顺义区胜利街道义宾北区"));
       baseDAO.insert(new User(5, "stak", 1, "北京市顺义区胜利街道义宾北区"));
       baseDAO.insert(new User(6, "banna", 1, "北京市顺义区胜利街道义宾北区"));

//        BaseDAO baseDAO =  DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(Product.class);
//        baseDAO.insert(new Product(1, "苹果", "2018-09-09", "北京市顺义区胜利街道义宾北区"));
    }

    /**
     * 删除
     * @param view
     */
    public void onDel(View view) {
        BaseDAOImp baseDAO = DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(User.class, BaseDAOImp.class);
        User where = new User();
        where.setId(2);
        baseDAO.delete(where, true);
    }

    /**
     * 查询
     * @param view
     */
    public void onQuery(View view) {
        BaseDAOImp baseDAO = DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(User.class,BaseDAOImp.class);
      User where = new User();
//        where.setName("lixue");
        List<User> userList=  baseDAO.query(where, "address", 0, 3);
        for (User user : userList){
            Log.e("user: ", user.toString());
        }
    }

    /**
     * 更新
     * @param view
     */
    public void onUpdate(View view) {
        BaseDAOImp baseDAO = DAOFactory.getOurInstance(getApplicationContext()).getBaseDAO(User.class, BaseDAOImp.class);

        User where = new User();
        where.setName("lixue");
        User obj = new User();
        obj.setAddress("湖北省襄阳市樊城区中铁十一局机关大院");
        baseDAO.update(obj, where,true);
        Toast.makeText(getApplicationContext(), "完成....", Toast.LENGTH_SHORT).show();
    }
}
