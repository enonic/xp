package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.api.util.ImageHelper;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content/media")
@Produces("application/octet-stream")
@RolesAllowed("admin-login")
public final class ContentMediaResource
    implements AdminResource
{
    private ContentService contentService;

    private ContentTypeService contentTypeService;

    @GET
    @Path("{contentId}")
    public Response media( @PathParam("contentId") final String contentIdAsString )
        throws IOException
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        return doServeMedia( contentId, null );
    }

    @GET
    @Path("{contentId}/{identifier}")
    public Response media( @PathParam("contentId") final String contentIdAsString, @PathParam("identifier") final String identifier )
        throws IOException
    {

        final ContentId contentId = ContentId.from( contentIdAsString );

        return doServeMedia( contentId, identifier );
    }

    private Response doServeMedia( final ContentId contentId, final String identifier )
        throws IOException
    {
        final Content content = contentService.getById( contentId );

        if ( content == null )
        {
            throw new NotFoundWebException( String.format( "Content [%s] was not found", contentId ) );
        }

        if ( !( content instanceof Media ) )
        {
            throw new NotFoundWebException( String.format( "Content [%s] is not a media", contentId ) );
        }

        final Attachment attachment = resolveAttachment( identifier, (Media) content );
        if ( attachment == null )
        {
            throw new NotFoundWebException( String.format( "Content [%s] has no attachments", contentId ) );
        }
        else
        {
            final ByteSource binary = contentService.getBinary( contentId, attachment.getBinaryReference() );
            return Response.ok( binary.openStream(), attachment.getMimeType() ).build();
        }
    }

    private Attachment resolveAttachment( final String identifier, final Media media )
    {
        Attachment attachment;
        if ( StringUtils.isNotEmpty( identifier ) )
        {
            attachment = media.getAttachments().byName( identifier );
            if ( attachment == null )
            {
                attachment = media.getAttachments().byLabel( identifier );
            }
        }
        else
        {
            attachment = media.getSourceAttachment();
        }
        return attachment;
    }

    private ResolvedImage resolveResponseFromContentImageAttachment( final Content content, final Attachment attachment )
    {
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( content.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                final BufferedImage contentImage;
                try (final InputStream inputStream = binary.openStream())
                {
                    contentImage = ImageHelper.toBufferedImage( inputStream );
                }
                catch ( IOException e )
                {
                    throw Exceptions.unchecked( e );
                }

                return new ResolvedImage( contentImage, attachment.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }


    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService );
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

}
