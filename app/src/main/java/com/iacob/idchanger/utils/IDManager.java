package com.iacob.idchanger.utils;

import android.util.Log;

import com.iacob.idchanger.app_parser.ApplicationModel;
import com.iacob.idchanger.id_parser.rootCheck;

import java.util.ArrayList;
import java.util.Random;

public class IDManager {

    public static String randomize(boolean isAndroidPackage) {
        Random random = new Random();
        String nonAndroidRefs = "0123456789abcdef";
        String androidRefs = "0123456789ABCDEF";
        int androidRefsSize = 64;
        int nonAndroidRefsSize = 16;
        StringBuilder str2 = new StringBuilder();
        if (isAndroidPackage) {
            for (int i = 0; i < androidRefsSize; i++) {
                str2.append(androidRefs.charAt(random.nextInt(androidRefs.length())));
            }
        } else {
            for (int i = 0; i < nonAndroidRefsSize; i++) {
                str2.append(nonAndroidRefs.charAt(random.nextInt(nonAndroidRefs.length())));
            }
        }
        return str2.toString();
    }

    public static String buildXMLColumn(ApplicationModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("  <setting id=\"");
        sb.append(model.extendedInfo.id);
        sb.append("\" name=\"");
        sb.append(model.extendedInfo.name);
        sb.append("\" value=\"");
        sb.append(model.ID);
        sb.append("\" package=\"");
        sb.append(model.package_name);
        sb.append("\" defaultValue=\"");
        sb.append(model.defID);
        sb.append("\" defaultSysSet=\"");
        sb.append(model.extendedInfo.defaultSysSet);
        sb.append("\" tag=\"");
        sb.append(model.extendedInfo.tag);
        sb.append("\" />\n");
        return sb.toString();
    }

    public static boolean writeXMLToSystem(ArrayList<ApplicationModel> apps) {
        String str = "<?xml version='\\''1.0'\\'' encoding='\\''UTF-8'\\'' standalone='\\''yes'\\'' ?>\n<settings version=\"-1\">\n";
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        for (int i = 0; i < apps.size(); i++) {
            sb.append(buildXMLColumn(apps.get(i)));
        }
        str = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("</settings>");
        String sb3 = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append("echo '");
        sb4.append(sb3);
        sb4.append("' > /data/system/users/0/settings_ssaid.xml");
        //Debug Only
        //sb4.append("' > /sdcard/settings_ssaid.xml");
        //Log.d("Debug", sb4.toString());
        //Log.d("Result", rootCheck.read(sb4.toString()));
        return true;
    }
}
