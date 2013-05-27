package com.enonic.wem.portal;

import com.google.inject.servlet.ServletModule;

final class PortalServletModule
    extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve( "/site/*" ).with( PortalServlet.class );
    }
}
