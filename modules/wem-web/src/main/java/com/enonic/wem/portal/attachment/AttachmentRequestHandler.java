package com.enonic.wem.portal.attachment;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.portal.AbstractResource;

public class AttachmentRequestHandler
    extends AbstractResource
{
    private AttachmentService attachmentService;


    @GET
    @Path("{key}")
    public String handleKey( @PathParam("key") final String key )
    {
        getAttachmentRequest().setKey( key );

        return attachmentService.getAttachment( getAttachmentRequest() );
    }

    @GET
    @Path("{key}/{name}")
    public String handleKey( @PathParam("key") final String key, @PathParam("name") final String attachmentName )
    {
        getAttachmentRequest().setKey( key );
        getAttachmentRequest().setAttachmentName( attachmentName );

        return attachmentService.getAttachment( getAttachmentRequest() );
    }

    @GET
    @Path("{key}/label/{label}")
    public String handleWithLabel( @PathParam("key") final String key, @PathParam("label") final String label )
    {
        getAttachmentRequest().setKey( key );
        getAttachmentRequest().setLabel( label );

        return attachmentService.getAttachment( getAttachmentRequest() );
    }

    private AttachmentRequest getAttachmentRequest()
    {
        return this.resourceContext.getResource( AttachmentRequest.class );
    }

    @Inject
    public void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }
}
