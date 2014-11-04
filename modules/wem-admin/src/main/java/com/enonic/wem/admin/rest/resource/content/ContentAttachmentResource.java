package com.enonic.wem.admin.rest.resource.content;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.attachment.AttachmentListJson;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;


@Path(ResourceConstants.REST_ROOT + "content/attachment")
@Produces(MediaType.APPLICATION_JSON)
public final class ContentAttachmentResource
    implements JaxRsComponent
{
    private AttachmentService attachmentService;

    @GET
    @Path("all")
    public AttachmentListJson getAttachments( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Attachments attachments = attachmentService.getAll( contentId );
        return new AttachmentListJson( attachments );
    }

    public void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }
}
