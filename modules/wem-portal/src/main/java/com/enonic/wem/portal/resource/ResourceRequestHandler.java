package com.enonic.wem.portal.resource;

import com.enonic.wem.portal.AbstractResource;

public class ResourceRequestHandler
    extends AbstractResource
{
    /*
    private ResourceService resourceService;

    @GET
    public Response getResource()
    {
        final Resource resource = resourceService.getResource( getResourceRequest() );

        if ( resource == null )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }

        final MediaType mimeType = MediaTypes.instance().fromFile( resource.getName() );

        try
        {
            return Response.ok( resource.getByteSource().read(), mimeType.toString() ).build();
        }
        catch ( IOException e )
        {
            return Response.serverError().build();
        }
    }

    @Path("{pathElement}")
    public ResourceRequestHandler handlePathElement( @PathParam("pathElement") String pathElement )
    {
        final ResourceRequestHandler resource = this.resourceContext.getResource( ResourceRequestHandler.class );

        final ResourceRequest resourceRequest = getResourceRequest();
        resourceRequest.appendPathElement( pathElement );

        return resource;
    }

    private ResourceRequest getResourceRequest()
    {
        return this.resourceContext.getResource( ResourceRequest.class );
    }

    @Inject
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
    */
}
