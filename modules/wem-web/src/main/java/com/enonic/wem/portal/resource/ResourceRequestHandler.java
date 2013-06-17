package com.enonic.wem.portal.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.portal.AbstractResource;
import com.enonic.wem.web.util.MimeTypeResolver;

public class ResourceRequestHandler
    extends AbstractResource
{
    private ResourceService resourceService;

    private MimeTypeResolver mimeTypeResolver;

    @GET
    public Response getResource()
    {
        final Resource resource = resourceService.getResource( getStaticRequest() );

        final String mimeType = mimeTypeResolver.getMimeType( resource.getFile().getName() );

        try
        {
            return Response.ok( resource.getByteSource().read(), mimeType ).build();
        }
        catch ( IOException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Response.serverError().build();
        }
    }

    @Path("{pathElement}")
    public ResourceRequestHandler handlePathElement( @PathParam("pathElement") String pathElement )
    {
        final ResourceRequestHandler resource = this.resourceContext.getResource( ResourceRequestHandler.class );

        final ResourceRequest resourceRequest = getStaticRequest();
        resourceRequest.appendPathElement( pathElement );

        return resource;
    }

    private ResourceRequest getStaticRequest()
    {
        return this.resourceContext.getResource( ResourceRequest.class );
    }

    @Inject
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Inject
    public void setMimeTypeResolver( final MimeTypeResolver mimeTypeResolver )
    {
        this.mimeTypeResolver = mimeTypeResolver;
    }
}
