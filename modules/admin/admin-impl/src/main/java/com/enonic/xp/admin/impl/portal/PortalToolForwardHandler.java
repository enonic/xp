package com.enonic.xp.admin.impl.portal;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.RenderMode;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/todelete2"})
public final class PortalToolForwardHandler
    extends HttpServlet
{
    private final static String PREFIX = "/admin/tool";

    private final static String BASE_URI = "/admin/portal/admin";


    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI();
        final String newPath = path.substring( PREFIX.length() );
        forwardToPortal( newPath, req, res );
    }

    private void forwardToPortal( final String toolPath, final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final PortalAttributes portalAttributes = new PortalAttributes();
        portalAttributes.setBaseUri( BASE_URI );
        portalAttributes.setRenderMode( RenderMode.ADMIN );
        req.setAttribute( PortalAttributes.class.getName(), portalAttributes );

        final String path = "/portal/draft/_/tool" + ( StringUtils.isEmpty( toolPath ) ? "/" : toolPath );
        final RequestDispatcher dispatcher = req.getRequestDispatcher( path );
        dispatcher.forward( req, res );
    }
}
