package com.peait.schdule;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.peait.entity.MashiRepo;
import com.peait.entity.TerraceSpider;
import com.peait.mail.SendEmailService;
import com.peait.mapper.TerraceSpiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.font.TrueTypeGlyphMapper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 码市项目
 */
@Component
public class MashiSpider implements PageProcessor {

    @Resource
    private TerraceSpiderMapper terraceSpiderMapper;

    @Autowired
    private SendEmailService sendEmailService;

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36").addHeader("accept", "application/json");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {

        String html = page.getRawText();
        Map<String, Object> map = JSON.parseObject(html, Map.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (mapKey.equals("rewards")) {
                List<MashiRepo> mashiRepos = JSON.parseArray(mapValue.toString(), MashiRepo.class);
                CopyOnWriteArrayList<MashiRepo> mashiRepsCOW = new CopyOnWriteArrayList(mashiRepos);
                List<TerraceSpider> lists = terraceSpiderMapper.selectByTerraceName("mashi");
                //去重处理
                if (!lists.isEmpty()) {
                    for (TerraceSpider terraceSpider : lists) {
                        for (int i = 0; i < mashiRepsCOW.size(); i++) {
                            if (terraceSpider.getProjectTitle().equals(mashiRepsCOW.get(i).getName()) && terraceSpider.getProjectDescription().equals(mashiRepsCOW.get(i).getDescription())) {
                                mashiRepsCOW.remove(i);
                            }
                        }
                    }
                }
                //新增数据
                for (MashiRepo mashiRepo : mashiRepsCOW) {
                    TerraceSpider terraceSpider = new TerraceSpider();
                    terraceSpider.setId(UUID.randomUUID().toString());
                    terraceSpider.setIsSend((byte) 0);
                    terraceSpider.setTerraceName("mashi");
                    terraceSpider.setProjectTitle(mashiRepo.getName());
                    terraceSpider.setProjectDescription(mashiRepo.getDescription());
                    terraceSpiderMapper.insertSelective(terraceSpider);
                }

            }
        }
        sendEmailService.getEmail();
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void schdule() {
        Spider.create(this)
                //从"https://github.com/code4craft"开始抓
                .addUrl("https://codemart.com/api/project?")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

}
