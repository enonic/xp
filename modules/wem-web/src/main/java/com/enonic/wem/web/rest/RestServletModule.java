package com.enonic.wem.web.rest;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

final class RestServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve( "/admin/rest/*", "/dev/rest/*" ).with( GuiceContainer.class );
    }
}
