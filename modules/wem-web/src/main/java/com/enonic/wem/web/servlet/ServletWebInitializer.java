package com.enonic.wem.web.servlet;

import javax.servlet.ServletContext;

final class ServletWebInitializer
    implements WebInitializer
{
    @Override
    public void initialize( final ServletContext context )
    {
        context.addListener( new RequestContextListener() );
    }
}
