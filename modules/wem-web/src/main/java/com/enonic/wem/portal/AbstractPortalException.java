package com.enonic.wem.portal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AbstractPortalException
    extends WebApplicationException
{
    public AbstractPortalException( String message )
    {
        super( Response.status( Response.Status.NOT_FOUND ).entity( message ).type( MediaType.TEXT_PLAIN ).build() );
    }
}
