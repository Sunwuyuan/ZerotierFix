package dev.wuyuan.zerotierfix;

import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.google.android.material.color.DynamicColors;

import dev.wuyuan.zerotierfix.model.DaoMaster;
import dev.wuyuan.zerotierfix.model.DaoSession;
import dev.wuyuan.zerotierfix.model.ZTOpenHelper;

/**
 * 主程序入口
 *
 * @author kaaass
 */
public class ZerotierFixApplication extends MultiDexApplication {
    private DaoSession mDaoSession;

    public void onCreate() {
        super.onCreate();
        Log.i("Application", "Starting Application");
        
        // 启用 Material 3 动态取色 (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
        
        // 创建 DAO 会话
        this.mDaoSession = new DaoMaster(
                new ZTOpenHelper(this, "ztfixdb", null)
                        .getWritableDatabase()
        ).newSession();
    }

    public DaoSession getDaoSession() {
        return this.mDaoSession;
    }
}
