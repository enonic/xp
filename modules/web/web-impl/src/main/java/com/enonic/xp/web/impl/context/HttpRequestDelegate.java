package com.enonic.xp.web.impl.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;

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

    @Override
    public HttpSession getSession()
    {
        return getUnwrappedRequest( this ).getSession();
    }

    @Override
    public HttpSession getSession( final boolean create )
    {
        return getUnwrappedRequest( this ).getSession( create );
    }

    private static HttpServletRequest getUnwrappedRequest( final HttpServletRequest request )
    {
        if ( request instanceof HttpServletRequestWrapper )
        {
            final HttpServletRequest unwrapped = (HttpServletRequest) ( (HttpServletRequestWrapper) request ).getRequest();
            return getUnwrappedRequest( unwrapped );
        }

        return request;
    }
}
