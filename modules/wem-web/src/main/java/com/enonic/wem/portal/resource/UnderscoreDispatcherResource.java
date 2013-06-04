package com.enonic.wem.portal.resource;

import javax.ws.rs.Path;

public class UnderscoreDispatcherResource
    extends AbstractResource
{

    @Path("attachment")
    public AttachmentResource handleAttachment()
    {
        final AttachmentResource resource = this.resourceContext.getResource( AttachmentResource.class );
        return resource;
    }

    @Path("image")
    public ImageResource handleImage()
    {
        return this.resourceContext.getResource( ImageResource.class );
    }

    @Path("public")
    public StaticResource handleStatic()
    {
        return this.resourceContext.getResource( StaticResource.class );
    }


}
