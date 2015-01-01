package com.enonic.wem.portal.internal.underscore;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.internal.base.BaseResource;

@Path("/portal/{mode}/{workspace}/{contentPath:.+}/_/attachment")
public final class AttachmentResource
    extends BaseResource
{
    protected ContentService contentService;

    protected Workspace workspace;

    protected ContentPath contentPath;

    @PathParam("workspace")
    public void setWorkspace( final String value )
    {
        this.workspace = Workspace.from( value );
    }

    @PathParam("contentPath")
    public void setContentPath( final String value )
    {
        this.contentPath = ContentPath.from( value ).asAbsolute();
    }

    @GET
    @Path("id/{mediaId}/{nameOrLabel}")
    public Response getById( @PathParam("mediaId") final String mediaId, @PathParam("nameOrLabel") final String nameOrLabel )
        throws IOException
    {
        final Media media = getMedia( ContentId.from( mediaId ) );
        final Attachment attachment = resolveAttachment( media, nameOrLabel );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found for Media [%s]", nameOrLabel, mediaId );
        }

        final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for Media [%s]", attachment.getBinaryReference(), mediaId );
        }

        return Response.ok().type( attachment.getMimeType() ).entity( binary.openStream() ).build();
    }

    @GET
    @Path("id/{mediaId}")
    public Response getById( @PathParam("mediaId") final String mediaId )
        throws IOException
    {
        return getById( mediaId, "source" );
    }

    @GET
    @Path("{nameOrLabel}")
    public Response getByNameOrLabel( @PathParam("nameOrLabel") final String nameOrLabel )
        throws IOException
    {
        final Media media = getMedia( contentPath );
        final Attachment attachment = resolveAttachment( media, nameOrLabel );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found for Media [%s]", nameOrLabel, contentPath );
        }

        final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for Media [%s]", attachment.getBinaryReference(), contentPath );
        }

        return Response.ok().type( attachment.getMimeType() ).entity( binary.openStream() ).build();
    }

    @GET
    public Response getSource()
        throws IOException
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
        final Content content = this.contentService.getById( contentId );
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
        final Content content = this.contentService.getByPath( contentPath );
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
