package com.enonic.wem.portal.dispatch;

import javax.ws.rs.Path;

import com.enonic.wem.portal.AbstractResource;
import com.enonic.wem.portal.attachment.AttachmentResource;
import com.enonic.wem.portal.image.ImageResource;
import com.enonic.wem.portal.resource.StaticResource;

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
