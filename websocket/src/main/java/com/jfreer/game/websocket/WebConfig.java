package com.jfreer.game.websocket;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午2:45
 */
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebSocketConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    protected Filter[] getServletFilters() {
        CharacterEncodingFilter charsetFilter = new CharacterEncodingFilter();
        charsetFilter.setForceEncoding(true);
        charsetFilter.setEncoding("UTF8");
        return new Filter[]{charsetFilter};
    }
}
