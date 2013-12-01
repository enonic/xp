package com.enonic.wem.admin.rest.resource.schema;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InvalidSchemaTypeException
    extends WebApplicationException
{
    public InvalidSchemaTypeException( String message )
    {
        super( Response.status( Response.Status.NOT_ACCEPTABLE ).entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
    }
}
