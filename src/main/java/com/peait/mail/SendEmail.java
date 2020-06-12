package com.peait.mail;

import com.gxf.reptile.entity.ZhubajieSpider;
import com.gxf.reptile.mapper.ZhubajieSpiderMapper;
import com.gxf.reptile.redis.RedisService;
import com.gxf.reptile.util.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SendEmail {
    @Resource
    private ZhubajieSpiderMapper zhubajieSpiderMapper;
    @Autowired
    private RedisService redisService;
    @Scheduled(cron = "0 0/1 * * * ?")
    private void pushKey(){
        log.info("开始发送邮件通知");
        //前端
        redisService.pushL("https://task.zbj.com/?t=1&so=1&ss=0&k=%E5%89%8D%E7%AB%AF");
        //后端
        redisService.pushL("https://task.zbj.com/?t=1&so=1&ss=0&k=%E5%90%8E%E7%AB%AF");
        //网站
        redisService.pushL("https://task.zbj.com/?t=1&so=1&ss=0&k=%E7%BD%91%E7%AB%99");
        //小程序
        redisService.pushL("https://task.zbj.com/?t=1&so=1&ss=0&k=%E5%B0%8F%E7%A8%8B%E5%BA%8F");
        //采集
        redisService.pushL("https://task.zbj.com/?t=1&so=1&ss=0&k=%E9%87%87%E9%9B%86");
        log.info("发送完成");
    }
    @Scheduled(cron = "0 0/1 * * * ?")
    private void getEmail(){
        log.info("开始发送邮件通知");
        List<ZhubajieSpider> zhubajieSpiders = zhubajieSpiderMapper.selectBySend();
        if(zhubajieSpiders!=null && zhubajieSpiders.size()>0){
            try {
                for (ZhubajieSpider zhubajie:zhubajieSpiders) {

                    String mailbody = zhubajie.getContentDetail()+" "+zhubajie.getsHref();

                    SendMail themail = new SendMail("smtp.163.com");

                    themail.setNeedAuth(true);

                    if (themail.setSubject(zhubajie.getTitleValue()+" "+zhubajie.getsHref()) == false){
                        return;
                    }


                    if (themail.setBody(mailbody) == false){
                        return;
                    }

                    if (themail.setTo("18631279680@163.com") == false){
                        return;
                    }

                    if (themail.setFrom("18631279680@163.com") == false){
                        return;
                    }

                    themail.setNamePass("18631279680@163.com", "feng920620");   // 账号密码

                    if (themail.sendout() == false){
                        return;
                    }
                    zhubajie.setIsSend(1);
                    zhubajieSpiderMapper.updateByPrimaryKey(zhubajie);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("发送完成");
    }
}
