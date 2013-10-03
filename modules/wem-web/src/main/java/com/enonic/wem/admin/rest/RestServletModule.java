package com.enonic.wem.admin.rest;

import com.google.inject.servlet.ServletModule;

final class RestServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        // Main rest path (should be the only one).
        serve( "/admin/rest/*" ).with( RestServlet.class );

        // Temporary rest path's (should be deleted before production).
        serve( "/dev/rest/*", "/old-admin/rest/*", "/admin/apps/space-manager/rest/*" ).with( RestServlet.class );
    }
}
