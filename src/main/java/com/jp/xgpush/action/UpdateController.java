package com.jp.xgpush.action;

import com.jp.xgpush.dao.DBDao;
import com.jp.xgpush.entity.VersionInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

@Controller
@RequestMapping("/update")
public class UpdateController {



    /**
     * 查询项目名为project的最新apk信息
     *
     * @param project
     * @return
     */
    @GetMapping("/version/lastest/{project}")
    @ResponseBody
    public VersionInfo queryLastestVersionCode(@PathVariable("project") String project) {
        VersionInfo info = DBDao.queryLastestVersion(project);
        return info;
    }

    /**
     * 更新项目名为project的最新apk信息
     *
     * @param project
     * @return
     */
    @GetMapping("/lastest/{project}/{versionCode}/{versionName}/{apkURL}")
    @ResponseBody
    public boolean updateLastestVersionCode(@PathVariable("project") String project,
                                            @PathVariable("versionCode") int versionCode,
                                            @PathVariable("versionName") String versionName,
                                            @PathVariable("apkURL") String apkURL) {

        VersionInfo info = new VersionInfo().setVersionCode(versionCode)
                .setVersionName(versionName)
                .setProject(project);
        if (!StringUtils.isEmpty(apkURL)){
            apkURL = "http://"+apkURL.replace("-", "/");
            info.setApkURL(apkURL);
        }

        return DBDao.updateVersionInfo(info);
    }
}
