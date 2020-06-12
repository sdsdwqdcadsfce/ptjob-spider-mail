package com.peait.schdule;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.peait.entity.MashiRepo;
import com.peait.entity.TerraceSpider;
import com.peait.mapper.TerraceSpiderMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.ibatis.ognl.DynamicSubscript.all;

/**
 * 猿急送
 */
@Component
public class YuanJiSongSpider implements PageProcessor {

    @Resource
    private TerraceSpiderMapper terraceSpiderMapper;

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36").addHeader("accept", "application/json");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {

        Selectable xpath = page.getHtml().xpath("//*[@class=\"weui_panel weui_panel_access weui_panel_access_adapt db_adapt margin-top-2\"]");
        List<String> titleList = xpath.xpath("//*[@class=\"topic_title\"]/text()").all();
        List<String> contentList = xpath.xpath("//*[@class=\"media_desc_adapt\"]/text()").all();
        CopyOnWriteArrayList<String> contentListCOW = new CopyOnWriteArrayList<>(contentList);
        for (String item:contentListCOW) {
            if (item.equals("") || item.length() < 1) {
                contentListCOW.remove(item);
            }
        }
        for (int i = 0; i < contentListCOW.size(); i++) {
            List<TerraceSpider> ysj = terraceSpiderMapper.selectByTerraceName("ysj");
            Boolean flag = true;
            if(!ysj.isEmpty()){
                for (TerraceSpider te:ysj) {
                    if(te.getProjectDescription().equals(contentListCOW.get(i))){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    TerraceSpider terraceSpider = new TerraceSpider();
                    terraceSpider.setId(UUID.randomUUID().toString());
                    terraceSpider.setIsSend((byte)0);
                    terraceSpider.setTerraceName("yjs");
                    terraceSpider.setProjectTitle(titleList.get(i));
                    terraceSpider.setProjectDescription(contentListCOW.get(i));
                    terraceSpiderMapper.insertSelective(terraceSpider);
                }
            }else {
                TerraceSpider terraceSpider = new TerraceSpider();
                terraceSpider.setId(UUID.randomUUID().toString());
                terraceSpider.setIsSend((byte)0);
                terraceSpider.setTerraceName("yjs");
                terraceSpider.setProjectTitle(titleList.get(i));
                terraceSpider.setProjectDescription(contentListCOW.get(i));
                terraceSpiderMapper.insertSelective(terraceSpider);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void schdule() {
        Spider.create(this)
                //从"https://github.com/code4craft"开始抓
                .addUrl("https://www.yuanjisong.com/job")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

}

