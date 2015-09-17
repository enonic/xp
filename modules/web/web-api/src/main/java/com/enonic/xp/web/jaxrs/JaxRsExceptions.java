package com.enonic.xp.web.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
        final String formatted = args.length > 0 ? String.format( message, args ) : message;
        return new WebApplicationException( formatted, status );
    }
}
