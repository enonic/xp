package com.enonic.xp.portal.impl.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=30", "osgi.http.whiteboard.filter.dispatcher=FORWARD",
        "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public final class AuthFilter
    extends OncePerRequestFilter
{
    private AuthControllerService authControllerService;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final AuthControllerWorker authControllerWorker = new AuthControllerWorker( authControllerService, req );

        // If the current user is not authenticated
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            //Execute the function authFilter of the IdProvider
            authControllerWorker.execute( "authFilter" );
        }

        //Wraps the response to handle 403 errors
        final AuthResponseWrapper responseWrapper = new AuthResponseWrapper( res, authControllerWorker );
        chain.doFilter( req, responseWrapper );

    }

    @Reference
    public void setAuthControllerService( final AuthControllerService authControllerService )
    {
        this.authControllerService = authControllerService;
    }
}
