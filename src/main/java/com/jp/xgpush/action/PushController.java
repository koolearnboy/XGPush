package com.jp.xgpush.action;

import com.jp.xgpush.App;
import com.jp.xgpush.dao.DBDao;
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
import java.util.Date;

/**
 * @author xiaojx
 * 信鸽推送
 */
@Controller
@RequestMapping("xg")
public class PushController {

    /**
     * 注册绑定iemi 和 token
     * @param iemi
     * @param token
     * @param project
     * @return
     */
    @GetMapping("/register/{iemi}/{token}/{project}")
    @ResponseBody
    public ResultEntity registerIEMIandToken(@PathVariable("iemi")String iemi,
                                             @PathVariable("token")String token,
                                             @PathVariable("project")String project){
        ResultEntity rs = new ResultEntity();
        TokenEntity tokenEntity = TokenEntity.of(token,iemi)
                .setProject(project)
                .setAddTime(new Date());
        DBDao.addToken(tokenEntity);
        rs.setSuccess(true);
        return rs;
    }

    /**
     * 发送消息
     * @param iemi
     * @param title
     * @param content
     * @return
     */
    @GetMapping("/push/{iemi}/{title}/{content}")
    @ResponseBody
    public ResultEntity pushMessage(@PathVariable("iemi")String iemi,
                                             @PathVariable("title")String title,
                                             @PathVariable("content")String content){
        ResultEntity rs = new ResultEntity();
        //查询iemi对应的token
        String token = DBDao.queryToken(iemi);
        String appId = App.prop.getProperty("appId");
        String secretKey = App.prop.getProperty("secretKey");
        System.out.println("发送消息: "+ iemi + "--"+token+ " --" + title);
        XGMessagePushService service = new XGMessagePushService(appId,secretKey);
        XGMessagePushService.Message message = new XGMessagePushService.Message();
        message.setTitle(title);
        message.setContent(content);
        JSONObject param = new JSONObject()
                .put("key1","val1");
        message.setParams(param);
        try {
            JSONObject jsonObject = service.sendSingleDevice(message,new String[]{token});
            if (jsonObject!=null && (jsonObject.getInt("ret_code") == 0)){
                rs.setSuccess(true);
                return rs;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }


    public static void main(String[] args) throws IOException {


    }


}
