package com.enonic.wem.web.rest;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import com.enonic.wem.web.rest.filter.ResponseCorsFilter;

final class RestServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        final Map<String, String> initProps = Maps.newHashMap();
        initProps.put( "com.sun.jersey.config.feature.DisableWADL", "true" );
        initProps.put( "com.sun.jersey.spi.container.ContainerResponseFilters", ResponseCorsFilter.class.getName() );

        serve( "/admin/rest/*", "/dev/rest/*" ).with( GuiceContainer.class, initProps );
    }
}
