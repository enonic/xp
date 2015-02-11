package com.enonic.wem.admin.rest.resource.repo;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;

@Path(ResourceConstants.REST_ROOT + "repo")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true)
public class IndexResource
    implements AdminResource
{


    @POST
    @Path("reindex")
    public ReindexResultJson reindex()
    {

        return null;
    }


}
