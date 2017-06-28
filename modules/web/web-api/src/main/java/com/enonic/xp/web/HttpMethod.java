package com.enonic.xp.web;

import java.util.EnumSet;

public enum HttpMethod
{
    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE, CONNECT, PATCH, PROPFIND, PROPPATCH, MKCOL, COPY, MOVE, LOCK, UNLOCK;

    public static EnumSet<HttpMethod> all()
    {
        return EnumSet.allOf( HttpMethod.class );
    }

    public static EnumSet<HttpMethod> standard()
    {
        return EnumSet.of( GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE );
    }
}
