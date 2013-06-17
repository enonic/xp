package com.enonic.wem.portal.dispatch;

import javax.ws.rs.Path;

import com.enonic.wem.portal.AbstractResource;
import com.enonic.wem.portal.attachment.AttachmentRequestHandler;
import com.enonic.wem.portal.image.ImageRequestHandler;
import com.enonic.wem.portal.resource.ResourceRequestHandler;

public class UnderscoreDispatcherResource
    extends AbstractResource
{

    @Path("attachment")
    public AttachmentRequestHandler handleAttachment()
    {
        final AttachmentRequestHandler resource = this.resourceContext.getResource( AttachmentRequestHandler.class );
        return resource;
    }

    @Path("image")
    public ImageRequestHandler handleImage()
    {
        return this.resourceContext.getResource( ImageRequestHandler.class );
    }

    @Path("public")
    public ResourceRequestHandler handleStatic()
    {
        return this.resourceContext.getResource( ResourceRequestHandler.class );
    }


}
