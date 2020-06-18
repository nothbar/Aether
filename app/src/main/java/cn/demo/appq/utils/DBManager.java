package cn.demo.appq.utils;

import com.github.megatronking.netbare.L;

import cn.demo.appq.greendao.DaoMaster;
import cn.demo.appq.greendao.DaoSession;

public class DBManager {
    private static volatile DaoSession instance = null;

    private DBManager() {
    }

    public static DaoSession getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = DaoMaster.newDevSession(L.getContext(null),"log_db");
                }
            }
        }
        return instance;
    }
}
