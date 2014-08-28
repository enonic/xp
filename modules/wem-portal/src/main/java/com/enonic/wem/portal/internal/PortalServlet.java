package com.enonic.wem.portal.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServerServlet;
import org.restlet.ext.servlet.ServletUtils;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

@Singleton
public final class PortalServlet
    extends ServerServlet
{
    @Inject
    protected PortalApplication portalApplication;

    @Override
    protected Component createComponent()
    {
        final String contextPath = getServletContext().getContextPath();
        final String attachPath = contextPath + ( contextPath.endsWith( "/" ) ? "portal" : "/portal" );

        final Component component = new Component()
        {
            @Override
            public void handle( final Request request, final Response response )
            {
                request.getAttributes().put( "__contextPath", contextPath );
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

        component.getDefaultHost().attach( attachPath, this.portalApplication );
        return component;
    }
}
