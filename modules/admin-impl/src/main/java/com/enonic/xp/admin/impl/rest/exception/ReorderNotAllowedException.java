package com.enonic.xp.admin.impl.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ReorderNotAllowedException
    extends WebApplicationException
{
    public ReorderNotAllowedException( final String message )
    {
        super( Response.status( Response.Status.BAD_REQUEST ).entity( message ).type( "text/plain" ).build() );
    }
}
