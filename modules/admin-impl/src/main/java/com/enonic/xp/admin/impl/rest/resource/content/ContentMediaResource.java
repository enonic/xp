package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.AdminResource;
import com.enonic.xp.admin.impl.rest.exception.NotFoundWebException;
import com.enonic.xp.admin.rest.resource.ResourceConstants;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.Exceptions;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content/media")
@Produces("application/octet-stream")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
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
            Response.ResponseBuilder response = Response.ok( binary.openStream(), attachment.getMimeType() );

            final String fileName = attachment.getName();
            if ( isNotEmpty( fileName ) )
            {
                response = response.header( "Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" +
                    URLEncoder.encode( fileName, "UTF-8" ) );
            }
            return response.build();
        }
    }

    private Attachment resolveAttachment( final String identifier, final Media media )
    {
        Attachment attachment;
        if ( isNotEmpty( identifier ) )
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

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
