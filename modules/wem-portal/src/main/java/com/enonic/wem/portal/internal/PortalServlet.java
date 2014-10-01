package com.enonic.wem.portal.internal;

import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServerServlet;
import org.restlet.ext.servlet.ServletUtils;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

public final class PortalServlet
    extends ServerServlet
{
    private PortalApplication application;

    @Override
    protected Component createComponent()
    {
        final Component component = new Component()
        {
            @Override
            public void handle( final Request request, final Response response )
            {
                try
                {
                    ServletRequestHolder.setRequest( ServletUtils.getRequest( request ) );
                    super.handle( request, response );
                }
                finally
                {
                    ServletRequestHolder.setRequest( null );
                }

            }
        };

        component.getDefaultHost().attach( "/portal", this.application );
        return component;
    }

    public void setPortalApplication( final PortalApplication application )
    {
        this.application = application;
    }
}
