package com.enonic.xp.portal.impl.idprovider;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api"})
@Order(-30)
@WebFilter("/*")
public final class IdProviderFilter
    extends OncePerRequestFilter
{
    private final IdProviderControllerService idProviderControllerService;

    @Activate
    public IdProviderFilter( @Reference final IdProviderControllerService idProviderControllerService )
    {
        this.idProviderControllerService = idProviderControllerService;
    }

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
        final HttpServletResponse response = DispatchConstants.XP_CONNECTOR.equals( req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) )
            ? new IdProviderResponseWrapper( idProviderControllerService, req, res )
            : res;

        final IdProviderRequestWrapper requestWrapper = new IdProviderRequestWrapper( req );

        chain.doFilter( requestWrapper, response );
    }
}
