package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.WebApplicationException;

import com.sun.jersey.api.Responses;

public class NotFoundWebException
    extends WebApplicationException
{
    public NotFoundWebException( final String message )
    {
        super( Responses.notFound().entity( message ).type( "text/plain" ).build() );
    }
}
