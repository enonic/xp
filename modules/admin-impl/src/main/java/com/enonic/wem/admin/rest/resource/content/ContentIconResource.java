package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.thumb.Thumbnail;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;
import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleSquareFilter;


@Path(ResourceConstants.REST_ROOT + "content/icon")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
public final class ContentIconResource
    implements AdminResource
{
    private static final ContentImageHelper helper = new ContentImageHelper();

    private ContentService contentService;

    @GET
    @Path("{contentId}")
    public Response getContentIcon( @PathParam("contentId") final String contentIdAsString,
                                    @QueryParam("size") @DefaultValue("128") final int size,
                                    @QueryParam("crop") @DefaultValue("true") final boolean crop, @QueryParam("ts") final String timestamp )
        throws Exception
    {

        if ( contentIdAsString == null )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content content = contentService.getById( contentId );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        ResolvedImage resolvedImage = resolveResponseFromThumbnail( content, size, crop );
        if ( resolvedImage.isOK() )
        {
            final boolean cacheForever = StringUtils.isNotEmpty( timestamp );
            if ( cacheForever )
            {
                final CacheControl cacheControl = new CacheControl();
                cacheControl.setMaxAge( Integer.MAX_VALUE );
                return resolvedImage.toResponse( cacheControl );
            }
            else
            {
                return resolvedImage.toResponse();
            }
        }
        else
        {
            if ( content instanceof Media )
            {
                final Media media = (Media) content;
                if ( media.isImage() )
                {
                    resolvedImage = resolveResponseFromImageAttachment( media, size );
                    if ( resolvedImage.isOK() )
                    {
                        return resolvedImage.toResponse();
                    }
                }
            }
        }

        throw new WebApplicationException( Response.Status.NOT_FOUND );
    }

    private ResolvedImage resolveResponseFromThumbnail( final Content content, final int size, final boolean crop )
    {
        final Thumbnail contentThumbnail = content.getThumbnail();

        if ( contentThumbnail != null )
        {
            final ByteSource binary = contentService.getBinary( content.getId(), contentThumbnail.getBinaryReference() );
            if ( binary != null )
            {
                ImageFilter filter = crop ? ScaleSquareFilter : ScaleMax;
                final BufferedImage thumbnailImage = helper.readImage( binary, size, filter );
                return new ResolvedImage( thumbnailImage, contentThumbnail.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromImageAttachment( final Media media, final int size )
    {
        final Attachment attachment = media.getMediaAttachment();
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                final BufferedImage contentImage = helper.readImage( binary, size, ScaleSquareFilter );
                return new ResolvedImage( contentImage, attachment.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
