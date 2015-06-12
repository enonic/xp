package com.enonic.xp.web;

import javax.servlet.http.HttpServletRequest;

public final class WebRequest
{
    private final HttpServletRequest raw;

    private WebRequest( final HttpServletRequest raw )
    {
        this.raw = raw;
    }

    public String getPath()
    {
        return this.raw.getPathInfo();
    }

    public HttpMethod getMethod()
    {
        // TODO: Return the http method. Do a lazy create on first access.
        return null;
    }

    public HttpHeaders getHeaders()
    {
        // TODO: Return the headers. Do a lazy create on first access.
        return null;
    }

    public final HttpServletRequest getRawRequest()
    {
        return this.raw;
    }

    public static WebRequest from( final HttpServletRequest req )
    {
        return new WebRequest( req );
    }
}
