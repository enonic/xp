package com.enonic.xp.portal.impl.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api"})
@Order(-30)
@WebFilter("/*")
public final class AuthFilter
    extends OncePerRequestFilter
{
    private AuthControllerService authControllerService;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        // If the current user is not authenticated
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            //Executes the function autoLogin of the IdProvider
            AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
                functionName( "autoLogin" ).
                servletRequest( req ).
                build();
            authControllerService.execute( executionParams );
        }

        //Wraps the response to handle 403 errors
        final AuthResponseWrapper responseWrapper = new AuthResponseWrapper( authControllerService, req, res );
        chain.doFilter( req, responseWrapper );

    }

    @Reference
    public void setAuthControllerService( final AuthControllerService authControllerService )
    {
        this.authControllerService = authControllerService;
    }
}
