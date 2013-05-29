package com.enonic.wem.admin.rest;

import com.google.inject.servlet.ServletModule;

final class RestServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve( "/admin/rest/*", "/dev/rest/*", "/admin2/apps/rest/*" ).with( RestServlet.class );
    }
}
