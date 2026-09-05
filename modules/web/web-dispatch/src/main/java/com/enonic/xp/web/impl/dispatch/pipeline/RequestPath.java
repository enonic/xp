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
     * Returns the decoded path of the request within the context.
     * <p>
     * This is the path the servlet specification maps servlets, filters and security constraints on: the
     * container strips the path parameters, decodes the {@code %nn} octets and removes the dot segments
     * before it routes anything, and rejects the sequences that would make the result ambiguous - an encoded
     * {@code /}, an encoded dot segment, a dot segment carrying a path parameter - with a 400. It is also
     * the path the rest of XP routes on, from the virtual host resolver down.
     * <p>
     * The raw {@code getRequestURI()} is not: it still carries the path parameters and dot segments that the
     * container has already normalized away, so {@code /foo/../admin/tool} does not begin with
     * {@code /admin/} there although that is what the request resolves to, and {@code /health;jsessionid=x}
     * does not equal {@code /health}.
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
