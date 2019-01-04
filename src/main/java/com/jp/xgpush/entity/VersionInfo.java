package com.jp.xgpush.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class VersionInfo {

    private int versionCode;
    private String versionName;
    private String apkURL;
    private String project;
    private String addTime;
}
