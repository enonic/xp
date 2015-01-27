package com.enonic.xp.portal.impl.resource.attachment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class AttachmentResource
    extends BaseSubResource
{
    // Pattern should be inline/{id}/{name} where {name} is attachmentName
    // Or download/{id}/{name} where {name} is attachmentName. This will also set the right disposition header.

    @GET
    @Path("id/{mediaId}/{nameOrLabel}")
    public Response getById( @PathParam("mediaId") final String mediaId, @PathParam("nameOrLabel") final String nameOrLabel )
        throws Exception
    {
        final Media media = getMedia( ContentId.from( mediaId ) );
        final Attachment attachment = resolveAttachment( media, nameOrLabel );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found for Media [%s]", nameOrLabel, mediaId );
        }

        final ByteSource binary = this.services.getContentService().getBinary( media.getId(), attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for Media [%s]", attachment.getBinaryReference(), mediaId );
        }

        return Response.ok().type( attachment.getMimeType() ).entity( binary.openStream() ).build();
    }

    @GET
    @Path("id/{mediaId}")
    public Response getById( @PathParam("mediaId") final String mediaId )
        throws Exception
    {
        return getById( mediaId, "source" );
    }

    @GET
    @Path("{nameOrLabel}")
    public Response getByNameOrLabel( @PathParam("nameOrLabel") final String nameOrLabel )
        throws Exception
    {
        final Media media = getMedia( contentPath );
        final Attachment attachment = resolveAttachment( media, nameOrLabel );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found for Media [%s]", nameOrLabel, contentPath );
        }

        final ByteSource binary = this.services.getContentService().getBinary( media.getId(), attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for Media [%s]", attachment.getBinaryReference(), contentPath );
        }

        return Response.ok().type( attachment.getMimeType() ).entity( binary.openStream() ).build();
    }

    @GET
    public Response getSource()
        throws Exception
    {
        return getByNameOrLabel( "source" );
    }

    private Attachment resolveAttachment( final Media media, final String nameOrLabel )
    {
        final Attachments attachments = media.getAttachments();
        final Attachment attachment = attachments.byName( nameOrLabel );
        if ( attachment != null )
        {
            return attachment;
        }
        return attachments.byLabel( nameOrLabel );
    }

    private Media getMedia( final ContentId contentId )
    {
        final Content content = this.services.getContentService().getById( contentId );
        if ( content == null )
        {
            throw notFound( "Content with id [%s] not found", contentId.toString() );
        }

        if ( !( content instanceof Media ) )
        {
            throw notFound( "Content with id [%s] is not an Media", contentId.toString() );
        }
        return (Media) content;
    }

    private Media getMedia( final ContentPath contentPath )
    {
        final Content content = this.services.getContentService().getByPath( contentPath );
        if ( content == null )
        {
            throw notFound( "Content with path [%s] not found", contentPath.toString() );
        }

        if ( !( content instanceof Media ) )
        {
            throw notFound( "Content with path [%s] is not an Media", contentPath.toString() );
        }
        return (Media) content;
    }
}
