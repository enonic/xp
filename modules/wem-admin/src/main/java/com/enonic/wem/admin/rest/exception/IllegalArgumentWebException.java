package com.enonic.wem.admin.rest.exception;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class IllegalArgumentWebException
    extends WebApplicationException
{
    public IllegalArgumentWebException( final String message )
    {
        super( Response.status( Response.Status.BAD_REQUEST ).entity( message ).type( "text/plain" ).build() );
    }
}
