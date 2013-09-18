package com.enonic.wem.admin.rest.resource.space.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.space.SpaceName;

public class DuplicatedSpaceException
    extends WebApplicationException
{

    public DuplicatedSpaceException( SpaceName spaceName )
    {
        super( Response.status( Response.Status.NOT_ACCEPTABLE ).entity( "Space with name " + spaceName + " already exists" ).type(
            MediaType.TEXT_PLAIN_TYPE ).build() );
    }
}
