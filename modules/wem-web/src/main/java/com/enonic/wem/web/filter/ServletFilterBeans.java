package com.enonic.wem.web.filter;

import java.util.List;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.RequestContextFilter;

import com.google.common.collect.Lists;

@Configuration
public class ServletFilterBeans
{
    @Bean
    public CompositeFilter compositeFilter()
    {
        final List<Filter> filters = Lists.newArrayList();
        filters.add( requestContextFilter() );

        final CompositeFilter filter = new CompositeFilter();
        filter.setFilters( filters );
        return filter;
    }

    @Bean
    public RequestContextFilter requestContextFilter()
    {
        return new RequestContextFilter();
    }
}
