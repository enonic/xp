package com.enonic.xp.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Deprecated
public final class JaxRsExceptions
{
    public static WebApplicationException badRequest( final String message, final Object... args )
    {
        return newException( Response.Status.BAD_REQUEST, message, args );
    }

    public static WebApplicationException notFound( final String message, final Object... args )
    {
        return newException( Response.Status.NOT_FOUND, message, args );
    }

    public static WebApplicationException newException( final Response.Status status, final String message, final Object... args )
    {
        return new WebApplicationException( String.format( message, args ), status );
    }
}
