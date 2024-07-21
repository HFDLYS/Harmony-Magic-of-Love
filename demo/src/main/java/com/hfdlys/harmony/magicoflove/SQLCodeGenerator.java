package com.hfdlys.harmony.magicoflove;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class SQLCodeGenerator {
    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 数据源配置
        mpg.setDataSource(new DataSourceConfig().setUrl("jdbc:mysql://101.43.129.190:3306/h-lom?serverTimezone=UT")
                .setDriverName("com.mysql.cj.jdbc.Driver").setUsername("h-lom").setPassword("h-lom233"));

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(System.getProperty("user.dir") + "/demo/src/main/java");
        globalConfig.setAuthor("Jiasheng Wang");
        globalConfig.setOpen(false);
        globalConfig.setFileOverride(true);
        globalConfig.setMapperName("%sMapper");
        globalConfig.setXmlName("%sMapper");
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.hfdlys.harmony.magicoflove.database");
        packageConfig.setEntity("entity");
        packageConfig.setMapper("mapper");
        packageConfig.setXml("resources.mapper");
        StrategyConfig strategyConfig = new StrategyConfig();
        //strategyConfig.setInclude("user");
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setRestControllerStyle(true);
        strategyConfig.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategyConfig);
        mpg.setPackageInfo(packageConfig);
        mpg.setGlobalConfig(globalConfig);

        mpg.execute();
    }
}
