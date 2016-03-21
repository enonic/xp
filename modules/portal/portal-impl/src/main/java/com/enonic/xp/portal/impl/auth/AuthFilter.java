package com.enonic.xp.portal.impl.auth;

import java.util.Optional;
import java.util.concurrent.Callable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=30", "osgi.http.whiteboard.filter.dispatcher=FORWARD",
        "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public final class AuthFilter
    extends OncePerRequestFilter
{
    private SecurityService securityService;

    private AuthDescriptorService authDescriptorService;

    private AuthControllerScriptFactory authControllerScriptFactory;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final String requestURI = req.getRequestURI();
        final Optional<PathGuard> pathGuardOptional = runWithAdminRole( () -> securityService.getPathGuardByPath( requestURI ) );

        //If there is a PathGuard for the current path
        if ( pathGuardOptional.isPresent() )
        {
            final PathGuard pathGuard = pathGuardOptional.get();
            final PathGuardResponseSerializer pathGuardResponseSerializer =
                new PathGuardResponseSerializer( req, securityService, authControllerScriptFactory, authDescriptorService, pathGuard );

            //If the PathGuard is passive or the user is authenticated
            if ( pathGuard.isPassive() || isAuthenticated() )
            {
                // Wraps the response to handle 401/403 errors
                final AuthResponseWrapper responseWrapper = new AuthResponseWrapper( res, pathGuardResponseSerializer );
                chain.doFilter( req, responseWrapper );
            }
            else
            {
                //Else, renders the auth controller
                final boolean responseSerialized = pathGuardResponseSerializer.serialize( res );

                //If the auth controller was not rendered
                if ( !responseSerialized )
                {
                    //Forwards the unmodified request and response
                    chain.doFilter( req, res );
                }
            }
        }
        else
        {
            //Else, forwards the unmodified request and response
            chain.doFilter( req, res );
        }
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }

    @Reference
    public void setAuthControllerScriptFactory( final AuthControllerScriptFactory authControllerScriptFactory )
    {
        this.authControllerScriptFactory = authControllerScriptFactory;
    }

    private boolean isAuthenticated()
    {
        final Context context = ContextAccessor.current();
        if ( context != null )
        {
            final AuthenticationInfo authInfo = context.getAuthInfo();
            return authInfo != null && authInfo.isAuthenticated();
        }
        return false;
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).
            principals( RoleKeys.ADMIN ).
            build();
        return ContextBuilder.from( context ).
            authInfo( authenticationInfo ).
            build().
            callWith( callable );
    }
}
