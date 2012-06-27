package com.enonic.wem.web.rest2;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

import com.enonic.wem.web.rest2.filter.AllowOriginFilter;

public final class JaxRsServlet
    extends SpringServlet
{
    private final List<String> requestFilters;

    private final List<String> responseFilters;

    private final List<String> resourceFilterFactories;

    public JaxRsServlet()
    {
        this.requestFilters = Lists.newArrayList();
        this.responseFilters = Lists.newArrayList();
        this.resourceFilterFactories = Lists.newArrayList();
        registerFilters();
    }

    private void registerFilters()
    {
        registerFilter( AllowOriginFilter.class );
    }

    private void registerFilter( final Class clz )
    {
        if ( ContainerRequestFilter.class.isAssignableFrom( clz ) )
        {
            this.requestFilters.add( clz.getName() );
        }

        if ( ContainerResponseFilter.class.isAssignableFrom( clz ) )
        {
            this.responseFilters.add( clz.getName() );
        }

        if ( ResourceFilterFactory.class.isAssignableFrom( clz ) )
        {
            this.resourceFilterFactories.add( clz.getName() );
        }
    }

    @Override
    protected void configure( final WebConfig wc, final ResourceConfig rc, final WebApplication wa )
    {
        final Map<String, Object> props = rc.getProperties();
        props.put( ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, getRequestFilters() );
        props.put( ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, getResponseFilters() );
        props.put( ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, getResourceFilterFactories() );
        super.configure( wc, rc, wa );
    }

    private String getRequestFilters()
    {
        return Joiner.on( "," ).join( this.requestFilters );
    }

    private String getResponseFilters()
    {
        return Joiner.on( "," ).join( this.responseFilters );
    }

    private String getResourceFilterFactories()
    {
        return Joiner.on( "," ).join( this.resourceFilterFactories );
    }
}
