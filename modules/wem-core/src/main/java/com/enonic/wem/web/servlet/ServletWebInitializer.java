package com.enonic.wem.web.servlet;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import com.enonic.wem.web.WebInitializer;
import com.enonic.wem.web.etag.ShallowEtagFilter;

final class ServletWebInitializer
    implements WebInitializer
{
    @Override
    public void initialize( final ServletContext context )
    {
        context.addListener( new RequestContextListener() );

//        final FilterRegistration.Dynamic reg = context.addFilter( "etag", new ShallowEtagFilter() );

//        final EnumSet<DispatcherType> dispatcherTypes = EnumSet.allOf( DispatcherType.class );
//        reg.addMappingForUrlPatterns( dispatcherTypes, false, "/admin/*" );
    }
}
