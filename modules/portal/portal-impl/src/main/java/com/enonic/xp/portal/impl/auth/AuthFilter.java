package com.enonic.xp.portal.impl.auth;

import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
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

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final String requestURI = req.getRequestURI();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( User.ANONYMOUS ).
            principals( RoleKeys.ADMIN ).
            build();
        final Optional<PathGuard> pathGuard = ContextBuilder.create().
            authInfo( authInfo ).
            build().
            callWith( () -> securityService.getPathGuardByPath( requestURI ) );

        if ( pathGuard.isPresent() )
        {
            final AuthResponseWrapper responseWrapper =
                new AuthResponseWrapper( req, res, authDescriptorService, errorHandlerScriptFactory, pathGuard.get() );
            chain.doFilter( req, responseWrapper );
        }
        else
        {
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
    public void setControllerScriptFactory( final ErrorHandlerScriptFactory errorHandlerScriptFactory )
    {
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
    }
}
