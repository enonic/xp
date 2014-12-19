package com.enonic.wem.servlet.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

final class HttpRequestDelegate
    extends HttpServletRequestWrapper
{
    public HttpRequestDelegate( final HttpServletRequest req )
    {
        super( req );
    }

    @Override
    public boolean isUserInRole( final String role )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ( authInfo != null ) && authInfo.hasRole( role );
    }
}
