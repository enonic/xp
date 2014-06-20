package com.enonic.wem.admin.rest.resource.content;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.attachment.AttachmentListJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;


@Path("content/attachment")
@Produces(MediaType.APPLICATION_JSON)
public class ContentAttachmentResource
{
    public static final Workspace STAGE_WORKSPACE = new Workspace( "stage" );

    static final Context STAGE_CONTEXT = new Context( STAGE_WORKSPACE );

    @Inject
    private AttachmentService attachmentService;


    @GET
    @Path("all")
    public AttachmentListJson getAttachments( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Attachments attachments = attachmentService.getAll( contentId, STAGE_CONTEXT );
        return new AttachmentListJson( attachments );
    }

}
