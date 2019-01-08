package com.jp.xgpush.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * GPS信息实体类
 * @author xiaojx
 */
@Setter
@Getter
@Accessors(chain = true)
public class GPSInfoEntity {
    private String iemi;
    private Double latitude;
    private Double longtitude;
    private Date time;
}
