package com.enonic.xp.portal.impl.resource.attachment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class AttachmentResource
    extends BaseSubResource
{
    @GET
    @Path("inline/{id}/{name}")
    public Response handleInline( @PathParam("id") final String id, @PathParam("name") final String name )
        throws Exception
    {
        return handleAttachment( id, name, false );
    }

    @GET
    @Path("download/{id}/{name}")
    public Response handleDownload( @PathParam("id") final String id, @PathParam("name") final String name )
        throws Exception
    {
        return handleAttachment( id, name, true );
    }

    private Response handleAttachment( final String id, final String name, final boolean download )
        throws Exception
    {
        return handleAttachment( ContentId.from( id ), name, download );
    }

    private Response handleAttachment( final ContentId id, final String name, final boolean download )
        throws Exception
    {
        final Content content = getContent( id );
        final Attachment attachment = resolveAttachment( content, name );
        final ByteSource binary = resolveBinary( id, attachment );

        final Response.ResponseBuilder response = Response.ok().type( attachment.getMimeType() ).entity( binary.openStream() );

        if ( download )
        {
            response.header( "Content-Disposition", "attachment; filename=" + attachment.getName() );
        }

        return response.build();
    }

    private Content getContent( final ContentId contentId )
    {
        final Content content = this.services.getContentService().getById( contentId );
        if ( content == null )
        {
            throw notFound( "Content with id [%s] not found", contentId.toString() );
        }

        return content;
    }

    private ByteSource resolveBinary( final ContentId id, final Attachment attachment )
    {
        final ByteSource binary = this.services.getContentService().getBinary( id, attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for [%s]", attachment.getBinaryReference(), id );
        }

        return binary;
    }

    private Attachment resolveAttachment( final Content content, final String name )
    {
        final Attachments attachments = content.getAttachments();
        final Attachment attachment = attachments.byName( name );
        if ( attachment != null )
        {
            return attachment;
        }

        throw notFound( "Attachment [%s] not found for [%s]", name, content.getPath() );
    }
}
