package com.jp.xgpush.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 对应数据库表 T_TOKEN_LIST
 * @author xiaojx
 */
@Accessors(chain = true)
@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class TokenEntity {
    private int id;
    @NonNull
    private String token;
    @NonNull
    private String iemi;
    /**
     * 扩展字段 用于表明属于那一个项目
     */
    private String project;
    private Date addTime;

}
