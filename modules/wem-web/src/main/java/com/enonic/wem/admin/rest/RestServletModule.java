package com.enonic.wem.admin.rest;

import com.google.inject.servlet.ServletModule;

final class RestServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve( "/admin/rest/*" ).with( RestServlet.class );
    }
}
