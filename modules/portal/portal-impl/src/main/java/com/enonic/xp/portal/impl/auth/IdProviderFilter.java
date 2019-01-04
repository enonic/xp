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
import com.enonic.xp.portal.auth.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.auth.IdProviderControllerService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class)
@Order(-30)
@WebFilter("/*")
public final class IdProviderFilter
    extends OncePerRequestFilter
{
    private IdProviderControllerService idProviderControllerService;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        // If the current user is not authenticated
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            //Executes the function autoLogin of the IdProvider
            IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
                functionName( "autoLogin" ).
                servletRequest( req ).
                build();
            idProviderControllerService.execute( executionParams );
        }

        //Wraps the response to handle 403 errors
        final IdProviderResponseWrapper responseWrapper = new IdProviderResponseWrapper( idProviderControllerService, req, res );
        chain.doFilter( req, responseWrapper );

    }

    @Reference
    public void setIdProviderControllerService( final IdProviderControllerService idProviderControllerService )
    {
        this.idProviderControllerService = idProviderControllerService;
    }
}
