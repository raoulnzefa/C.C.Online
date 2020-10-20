package com.leyou.goods.service;

import com.leyou.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    /**
     * crear página html
     * @param spuId
     */
    public void createHtml(Long spuId) {
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));
        PrintWriter printWriter = null;
        try {
            //把静态文件生成到服务器本地
            File file = new File("F:\\ideawork\\project0728\\tools\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item", context, printWriter);
        } catch (Exception e) {
            LOGGER.error("error with creat static pages: {}" + e, spuId );
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * crear un nuevo hilo para manipular static pages
     * @param spuId
     */
    public void asyncExecute (Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
    }


    public void deleteHtml(Long id) {
        File file = new File("F:\\ideawork\\project0728\\tools\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
