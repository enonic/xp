package com.enonic.xp.web;

import java.util.EnumSet;

public enum HttpMethod
{
    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE, CONNECT, PATCH, PROPFIND, PROPPATCH, MKCOL, COPY, MOVE, LOCK, UNLOCK;

    private static final EnumSet<HttpMethod> STANDARD_METHODS = EnumSet.of( GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE );

    public static EnumSet<HttpMethod> all()
    {
        return EnumSet.allOf( HttpMethod.class );
    }

    public static EnumSet<HttpMethod> standard()
    {
        return STANDARD_METHODS.clone();
    }
}
