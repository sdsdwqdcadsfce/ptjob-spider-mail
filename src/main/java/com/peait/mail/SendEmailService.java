package com.peait.mail;


import com.peait.entity.SendMail;
import com.peait.entity.TerraceSpider;
import com.peait.mapper.TerraceSpiderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SendEmailService {
    @Resource
    private TerraceSpiderMapper terraceSpiderMapper;

    public void   getEmail(){
        synchronized (this) {
            log.info("开始发送邮件通知");
            List<TerraceSpider> sendEmailLists = terraceSpiderMapper.selectBySend();
            if(sendEmailLists!=null && sendEmailLists.size()>0){
                try {
                    for (TerraceSpider terraceSpider:sendEmailLists) {

                        String mailbody = terraceSpider.getProjectDescription();

                        SendMail themail = new SendMail("smtp.163.com");

                        themail.setNeedAuth(true);

                        if (themail.setSubject(terraceSpider.getTerraceName()+terraceSpider.getProjectTitle()) == false){
                            return;
                        }


                        if (themail.setBody(mailbody) == false){
                            return;
                        }

                        if (themail.setTo("18631279680@163.com") == false){
                            return;
                        }
                        int i = RandomUtils.nextInt(1, 3);
                        if(i==1){
                            if (themail.setFrom("c18631279680@163.com") == false){
                                return;
                            }

                            themail.setNamePass("c18631279680@163.com", "SVVTSIULDCGSHTLT");   // 账号密码
                         }else {
                             if (themail.setFrom("b18631279680@163.com") == false){
                                 return;
                             }

                             themail.setNamePass("b18631279680@163.com", "OTXBTKVKJIAKEAQW");   // 账号密码
                         }

                        if (themail.sendout() == false){
                            return;
                        }
                        terraceSpider.setIsSend((byte) 1);
                        terraceSpiderMapper.updateByPrimaryKey(terraceSpider);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info("发送完成");
        }

    }
}
