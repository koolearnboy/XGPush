package com.jp.xgpush.action;

import com.jp.xgpush.App;
import com.jp.xgpush.dao.DBDao;
import com.jp.xgpush.entity.GPSInfoEntity;
import com.jp.xgpush.entity.ResultEntity;
import com.jp.xgpush.entity.TokenEntity;
import com.jp.xgpush.service.XGMessagePushService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaojx
 * GPS服务
 */
@Controller
@RequestMapping("gps")
public class GPSController {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 上传坐标信息
     * @param iemi
     * @param latitude 维度
     * @param longitude 经度
     * @return
     */
    @GetMapping("/upload/{iemi}/{latitude}/{longitude}")
    @ResponseBody
    public ResultEntity uploadGPSInfo(@PathVariable("iemi")String iemi,
                                             @PathVariable("latitude")Double latitude,
                                             @PathVariable("longitude")Double longitude){

        ResultEntity rs = new ResultEntity();
        System.out.println(format.format(new Date()) + ": "+iemi+" 纬度"+latitude+"-经度"+longitude);
        GPSInfoEntity entity = new GPSInfoEntity().setIemi(iemi)
                .setLatitude(latitude)
                .setLongtitude(longitude);
        boolean result = DBDao.addGPSInfo(entity);
        rs.setSuccess(result);
        return rs;
    }


}
