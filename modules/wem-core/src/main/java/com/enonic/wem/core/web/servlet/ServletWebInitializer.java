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
    }
}
