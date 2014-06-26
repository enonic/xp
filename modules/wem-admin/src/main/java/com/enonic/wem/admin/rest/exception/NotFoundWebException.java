package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NotFoundWebException
    extends WebApplicationException
{
    public NotFoundWebException( final String message )
    {
        super( Response.status( Response.Status.NOT_FOUND ).entity( message ).type( "text/plain" ).build() );
    }
}
