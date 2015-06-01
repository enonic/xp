package com.enonic.xp.admin.impl.portal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class PortalForwardHandler
    extends BaseWebHandler
{
    private final static String PREFIX = "/admin/portal";

    private final static String EDIT_PREFIX = PREFIX + "/edit/";

    private final static String PREVIEW_PREFIX = PREFIX + "/preview/";

    public PortalForwardHandler()
    {
        setOrder( 10 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return req.getRequestURI().startsWith( PREFIX );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
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

        res.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    private void forwardToPortal( final RenderMode mode, final String path, final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setBaseUri( PREFIX + "/" + mode.toString() );
        portalRequest.setMode( mode );

        PortalRequestAccessor.set( req, portalRequest );
        final RequestDispatcher dispatcher = req.getRequestDispatcher( "/portal/" + path );
        dispatcher.forward( req, res );
    }
}
