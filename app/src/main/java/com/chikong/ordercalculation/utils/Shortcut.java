package com.chikong.ordercalculation.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.MainApplication;
import com.chikong.ordercalculation.R;

import java.util.List;

/**
 * 创建桌面快捷方式
 * Created by Administrator on 16/03/07.
 */
public class Shortcut {

    private static String AUTHORITY = null;

    /**
     * 检查桌面图标，检测到首次安装、更新都创建图标
     * @param context
     */
    public static void checkShortCut(Context context) {
        // 是否首次安装
        boolean isFirstInstall = SettingUtils.getSetting(SettingUtils.FIRST_INSTALL,true);
        // 最后一次启动App的版本
        String lastLaunchVersion = SettingUtils.getSetting(SettingUtils.LAST_LAUNCH_VERSION, "");
        // 这次启动的版本
        String currentVersion = MainApplication.getInstance().getAppVersionName();

        // 首次安装、更新都创建图标
        if (isFirstInstall || "".equals(lastLaunchVersion) || !lastLaunchVersion.equals(currentVersion)) {
            int iconResId = R.mipmap.ic_launcher;
            // 创建快捷方式，兼容问题，可能会重复创建
            Shortcut.createShortcut(context,iconResId);

            SettingUtils.setSetting( SettingUtils.LAST_LAUNCH_VERSION, currentVersion);
            SettingUtils.setSetting( SettingUtils.FIRST_INSTALL, false);

        }
    }

    /**
     * 为当前应用添加桌面快捷方式
     *
     * @param context
     * @param iconResId 快捷方式图标
     */
    public static void createShortcut(Context context, int iconResId) {
        if (isShortcutExist(context,context.getString(R.string.app_name))) return;
        LogHelper.trace("create shortcut");
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Intent shortcutIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        // 点击快捷图片，运行程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // 获取当前应用名称
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        // 不允许重复创建（不一定有效）
        shortcut.putExtra("duplicate", false);
        // 快捷方式的图标
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(context, iconResId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        context.sendBroadcast(shortcut);

    }


    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param context
     */
    public static void delShortcut(Context context) {
        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        Intent shortcutIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        context.sendBroadcast(shortcut);
    }

    /** 是否已创建快捷方式
     * @param context
     * @param title
     * @return  true 当检测到图标已存在或者没有权限时
     */
    public static boolean isShortcutExist(Context context, String title) {

        boolean isInstallShortcut = false;

        if (null == context || TextUtils.isEmpty(title))   return isInstallShortcut;
        if (TextUtils.isEmpty(AUTHORITY))  AUTHORITY = getAuthorityFromPermission(context);

        final ContentResolver cr = context.getContentResolver();

        if (!TextUtils.isEmpty(AUTHORITY)) {
            try {
                final Uri CONTENT_URI = Uri.parse(AUTHORITY);

                Cursor c = cr.query(CONTENT_URI, new String[] { "title",
                                "iconResource" }, "title=?", new String[] { title },
                        null);

                if (c != null && c.getCount() > 0) {
                    isInstallShortcut = true;
                }
                if (c != null && !c.isClosed())  c.close();
            } catch (Exception e) {
                LogHelper.e(e.getMessage());
                isInstallShortcut = true;
            }

        }
        LogHelper.trace("shortcut is Exist ==> title = "+title+" result = "+isInstallShortcut);
        return isInstallShortcut;

    }

    public static String getCurrentLauncherPackageName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager()
                .resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return "";
        }
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

    public static String getAuthorityFromPermissionDefault(Context context) {

        return getThirdAuthorityFromPermission(context,
                "com.android.launcher.permission.READ_SETTINGS");
    }

    public static String getThirdAuthorityFromPermission(Context context,
                                                         String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }

        try {
            List<PackageInfo> packs = context.getPackageManager()
                    .getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null)    return "";
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission)
                                || permission.equals(provider.writePermission)) {
                            String authority = provider.authority;
//                            if (!StringUtils.isEmpty(authority) && (
//                                    authority .contains("launcher.settings")
//                                    || authority.contains("twlauncher.settings")
//                                    || authority.contains("launcher2.settings")))
                                return authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAuthorityFromPermission(Context context) {
        // 获取默认
        String authority = getAuthorityFromPermissionDefault(context);
        // 获取特殊第三方
        if (authority == null || authority.trim().equals("")) {
            String packageName = getCurrentLauncherPackageName(context);
            packageName += ".permission.READ_SETTINGS";
            authority = getThirdAuthorityFromPermission(context, packageName);
        }
        // 还是获取不到，直接写死
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                authority = "com.android.launcher.settings";
            } else if (sdkInt < 19) {// Android 4.4以下
                authority = "com.android.launcher2.settings";
            } else {// 4.4以及以上
                authority = "com.android.launcher3.settings";
            }
        }
        authority = "content://" + authority + "/favorites?notify=true";
        return authority;

    }

















}
