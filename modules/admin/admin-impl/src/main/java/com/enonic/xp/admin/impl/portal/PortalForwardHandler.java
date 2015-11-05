package com.enonic.xp.admin.impl.portal;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/admin/portal/*"})
public final class PortalForwardHandler
    extends HttpServlet
{
    private final static String PREFIX = "/admin/portal";

    private final static String EDIT_PREFIX = PREFIX + "/edit/";

    private final static String PREVIEW_PREFIX = PREFIX + "/preview/";

    private final static String ADMIN_PREFIX = PREFIX + "/admin/";


    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !req.isUserInRole( RoleKeys.ADMIN_LOGIN_ID ) )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        final String path = req.getRequestURI();
        if ( path.startsWith( EDIT_PREFIX ) )
        {
            final String newPath = path.substring( EDIT_PREFIX.length() );
            forwardToPortal( RenderMode.EDIT, newPath, req, res );
            return;
        }

        if ( path.startsWith( PREVIEW_PREFIX ) )
        {
            final String newPath = path.substring( PREVIEW_PREFIX.length() );
            forwardToPortal( RenderMode.PREVIEW, newPath, req, res );
            return;
        }

        if ( path.startsWith( ADMIN_PREFIX ) )
        {
            final String newPath = path.substring( ADMIN_PREFIX.length() );
            forwardToPortal( RenderMode.ADMIN, newPath, req, res );
            return;
        }

        res.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    private void forwardToPortal( final RenderMode renderMode, final String path, final HttpServletRequest req,
                                  final HttpServletResponse res )
        throws ServletException, IOException
    {
        final PortalAttributes portalAttributes = new PortalAttributes();
        portalAttributes.setBaseUri( PREFIX + "/" + renderMode.toString() );
        portalAttributes.setRenderMode( renderMode );
        req.setAttribute( PortalAttributes.class.getName(), portalAttributes );

        final RequestDispatcher dispatcher = req.getRequestDispatcher( "/portal/" + path );
        dispatcher.forward( req, res );
    }
}
