package com.enonic.xp.portal.impl.idprovider;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class IdProviderRequestWrapper
    extends HttpServletRequestWrapper
{
    private final AuthenticationInfo authInfo;

    private final Principal principal;

    IdProviderRequestWrapper( final HttpServletRequest req )
    {
        super( req );
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        this.authInfo = authInfo;
        this.principal = authInfo == null ? null : authInfo.getUser();
    }

    @Override
    public Principal getUserPrincipal()
    {
        return principal;
    }

    @Override
    public boolean isUserInRole( final String role )
    {
        return authInfo != null && authInfo.hasRole( role );
    }
}
