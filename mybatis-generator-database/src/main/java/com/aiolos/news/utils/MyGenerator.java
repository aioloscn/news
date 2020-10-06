package com.aiolos.news.utils;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aiolos
 * @date 2020/9/23 5:18 下午
 */
public class MyGenerator {

    public void generator() throws Exception {

        List<String> warning = new ArrayList<String>();
        File configFile = new File(this.getClass().getResource("/mybatis-generator.xml").getPath());
//        File configFile = new File("mybatis-generator-database" + File.separator + "mybatis-generator.xml");
        ConfigurationParser cp = new ConfigurationParser(warning);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warning);
        myBatisGenerator.generate(null);
    }

    public static void main(String[] args) {

        System.out.println("逆向生成中......");
        MyGenerator myGenerator = new MyGenerator();
        try {
            myGenerator.generator();
            System.out.println( "逆向生成成功啦~" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
