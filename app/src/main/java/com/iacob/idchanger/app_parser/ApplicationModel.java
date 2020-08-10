package com.iacob.idchanger.app_parser;

import android.graphics.drawable.Drawable;

public class ApplicationModel {

    public String app_name;
    public String package_name;
    public String ID, defID;
    public boolean modified = false;
    public Drawable icon;
    public ExtendedInfo extendedInfo;

    public ApplicationModel() {
    }

    public ExtendedInfo getExtendedInfo() {
        return extendedInfo;
    }

    public void setExtendedInfo(ExtendedInfo extendedInfo) {
        this.extendedInfo = extendedInfo;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDefID() {
        return defID;
    }

    public void setDefID(String defID) {
        this.defID = defID;
    }

    public boolean isModified() {
        return modified;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public static class ExtendedInfo {
        public String id, defaultSysSet, tag, name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getDefaultSysSet() {
            return defaultSysSet;
        }

        public String getId() {
            return id;
        }

        public void setDefaultSysSet(String defaultSysSet) {
            this.defaultSysSet = defaultSysSet;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
