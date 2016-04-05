package com.enonic.xp.portal.impl.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.security.SecurityService;
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

        //Wraps the response to handle 403 errors
        final PathGuardResponseSerializer pathGuardResponseSerializer =
            new PathGuardResponseSerializer( securityService, authControllerScriptFactory, authDescriptorService, req );
        final AuthResponseWrapper responseWrapper = new AuthResponseWrapper( res, pathGuardResponseSerializer );
        chain.doFilter( req, responseWrapper );
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
}
