package com.enonic.wem.core.web.servlet;

import javax.servlet.ServletContext;

import com.enonic.wem.core.web.WebInitializer;

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
