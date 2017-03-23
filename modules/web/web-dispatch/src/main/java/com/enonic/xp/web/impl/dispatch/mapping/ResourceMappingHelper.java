package com.enonic.xp.web.impl.dispatch.mapping;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.dispatch.ServletMapping;

final class ResourceMappingHelper
{
    static ServletMapping servlet( final Servlet servlet )
    {
        final WebServlet config = servlet.getClass().getAnnotation( WebServlet.class );
        if ( config == null )
        {
            return null;
        }

        final MappingBuilder builder = MappingBuilder.newBuilder();
        configure( builder, config );
        configureOrder( builder, servlet );
        return builder.servlet( servlet );
    }

    static FilterMapping filter( final Filter filter )
    {
        final WebFilter config = filter.getClass().getAnnotation( WebFilter.class );
        if ( config == null )
        {
            return null;
        }

        final MappingBuilder builder = MappingBuilder.newBuilder();
        configure( builder, config );
        configureOrder( builder, filter );
        return builder.filter( filter );
    }

    private static void configure( final MappingBuilder builder, final WebFilter config )
    {
        builder.name( config.filterName() );
        builder.urlPatterns( config.value() );
        builder.urlPatterns( config.urlPatterns() );
        addInitParams( builder, config.initParams() );
    }

    private static void configure( final MappingBuilder builder, final WebServlet config )
    {
        builder.name( config.name() );
        builder.urlPatterns( config.value() );
        builder.urlPatterns( config.urlPatterns() );
        addInitParams( builder, config.initParams() );
    }

    static void configureOrder( final MappingBuilder builder, final Object value )
    {
        final Order order = value.getClass().getAnnotation( Order.class );
        if ( order != null )
        {
            builder.order( order.value() );
        }
    }

    private static void addInitParams( final MappingBuilder builder, final WebInitParam... params )
    {
        for ( final WebInitParam param : params )
        {
            builder.initParam( param.name(), param.value() );
        }
    }
}
