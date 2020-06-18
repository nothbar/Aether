package cn.demo.appq.utils;

import com.github.megatronking.netbare.L;

import org.greenrobot.greendao.database.Database;

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
                    Database db = new DaoMaster.DevOpenHelper(L.getContext(null), "log_db").getWritableDb();
                    DaoMaster daoMaster = new DaoMaster(db);
                    //汇总APP使用流量排行
                    daoMaster.getDatabase().execSQL(
                            "DROP VIEW IF EXISTS SUM_VIEW;\n" +
                                    "CREATE VIEW IF NOT EXISTS SUM_VIEW AS\n" +
                                    "SELECT APP_NAME ,HOST,COUNT(*), SUM(LENGTH),MIN(TIME)\n" +
                                    "FROM  REQ_ENTITY GROUP BY HOST ORDER BY SUM(LENGTH) DESC;");
                    instance = daoMaster.newSession();
                }
            }
        }
        return instance;
    }
}
