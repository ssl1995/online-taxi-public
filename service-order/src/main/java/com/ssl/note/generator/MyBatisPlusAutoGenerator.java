package com.ssl.note.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/28 22:31
 * @Describe:
 */
public class MyBatisPlusAutoGenerator {
    public static final String TABLE_NAME = "order_info";


    /**
     * mybatis-plus逆向生成方法
     */
    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://rm-2zep5730lmk5s5dy37o.mysql.rds.aliyuncs.com:3306/service-order?characterEncoding=UTF8&autoReconnect=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
                        "root", "Ssl@199502")
                .globalConfig(builder -> {
                    builder.author("songshenglin").fileOverride().outputDir("/Users/didi/jetbrains/ideaProduct/online-taxi-public/service-order/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.ssl.note").pathInfo(Collections.singletonMap(OutputFile.mapperXml, "/Users/didi/jetbrains/ideaProduct/online-taxi-public/service-order/src/main/java/com/ssl/note/mapper"));
                })
                .strategyConfig(builder -> {
                    // 指定逆向生成表
                    builder.addInclude(TABLE_NAME);
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
