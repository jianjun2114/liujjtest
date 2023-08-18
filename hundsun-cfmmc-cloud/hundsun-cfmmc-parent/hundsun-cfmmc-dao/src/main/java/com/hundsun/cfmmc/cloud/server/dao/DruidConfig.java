package com.hundsun.cfmmc.cloud.server.dao;

import com.alibaba.druid.support.http.StatViewServlet;
import com.hundsun.jrescloud.common.util.ConfigUtils;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfig {
    /**
     * 注册一个关闭Druid监控页面的Bean
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        // 访问监控的用户；
        String loginName = ConfigUtils.get("hs.druid.statViewServlet.loginUsername", "cfmmc");
        // 访问监控的用户密码；
        String loginPwd = ConfigUtils.get("hs.druid.statViewServlet.loginPassword", "cfmmc");
        // 是否支持重置监控信息，默认true，界面上有重置按钮
        String resetEnable = ConfigUtils.get("hs.druid.statViewServlet.resetEnable", "true");

        StatViewServlet servlet = new StatViewServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/druid/*");
       // 设置初始化参数
        registrationBean.addInitParameter("loginUsername", loginName);
        registrationBean.addInitParameter("loginPassword", loginPwd);
        registrationBean.addInitParameter("resetEnable", resetEnable);

        return registrationBean;
    }
}
