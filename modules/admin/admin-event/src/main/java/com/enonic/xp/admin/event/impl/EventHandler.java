package com.enonic.xp.admin.event.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(immediate = true, service = {Servlet.class}, property = {"connector=xp"})
@Order(-100)
@WebServlet("/admin/event")
public final class EventHandler
    extends HttpServlet
{
    private final WebSocketService webSocketService;

    private final EndpointFactory endpointFactory;

    @Activate
    public EventHandler( @Reference final WebSocketService webSocketService, @Reference final EndpointFactory endpointFactory )
    {
        this.webSocketService = webSocketService;
        this.endpointFactory = endpointFactory;
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) && !req.isUserInRole( RoleKeys.ADMIN.getId() ) )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        if ( !this.webSocketService.isUpgradeRequest( req, res ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        this.webSocketService.acceptWebSocket( req, res, endpointFactory );
    }
}
