package com.peait.schdule;

import java.math.BigDecimal;

import com.peait.entity.TerraceSpider;
import com.peait.mail.SendEmailService;
import com.peait.mapper.TerraceSpiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 猪八戒
 */
@Component
public class ZhubajieSpider implements PageProcessor {

    @Resource
    private TerraceSpiderMapper terraceSpiderMapper;

    @Autowired
    private SendEmailService sendEmailService;

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36").addHeader("accept", "application/json");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {

        Selectable xpath = page.getHtml().xpath("//*[@class=\"demand-list\"]");
        List<String> all = xpath.xpath("//*[@class=\"demand-card-foot\"]/@data-link").all();
        // 部分三：从页面发现后续的url地址来抓取
        String title = page.getHtml().xpath("//*[@class=\"item-value value title\"]/text()").toString();
        if (title != null && !title.isEmpty()) {
            //*[@class="item-value value desc J-task-desc beforeSuccess"]/text()
            String body = page.getHtml().xpath("//*[@class=\"item-value value desc J-task-desc beforeSuccess\"]/text()").toString();
            System.out.println(title);
            System.out.println(body);
            System.out.println(page.getUrl());
            if (!title.contains("logo") && !title.contains("LOGO")) {
                TerraceSpider terraceSpider1 = terraceSpiderMapper.selectByPrimaryKey(page.getUrl().toString());
                if (terraceSpider1 == null) {
                    TerraceSpider terraceSpider = new TerraceSpider();
                    terraceSpider.setId(page.getUrl().toString());
                    terraceSpider.setIsSend((byte) 0);
                    terraceSpider.setTerraceName("zbj");
                    terraceSpider.setProjectTitle(title);
                    terraceSpider.setProjectDescription(body);
                    terraceSpiderMapper.insertSelective(terraceSpider);
                    sendEmailService.getEmail();
                }

            }

        }
        page.addTargetRequests(all);
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void schdule() {
        Spider.create(this)
                //从"https://github.com/code4craft"开始抓
                .addUrl("https://task.zbj.com/hall/bid/?m=1111&so=2&ss=0")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

}

