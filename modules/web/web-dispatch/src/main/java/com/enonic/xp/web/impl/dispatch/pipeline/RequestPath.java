package com.enonic.xp.web.impl.dispatch.pipeline;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The path a filter or servlet definition is matched against.
 */
final class RequestPath
{
    private RequestPath()
    {
    }

    /**
     * Returns the decoded path of the request within the context, which is the path the servlet
     * specification maps servlets, filters and security constraints on.
     */
    static String of( final HttpServletRequest req )
    {
        final String pathInfo = req.getPathInfo();
        if ( pathInfo == null )
        {
            return req.getServletPath();
        }

        final String servletPath = req.getServletPath();
        return servletPath.isEmpty() ? pathInfo : servletPath + pathInfo;
    }
}
