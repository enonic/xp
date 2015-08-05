package com.enonic.xp.admin.impl.rest.resource.content;

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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.security.RoleKeys;


@Path(ResourceConstants.REST_ROOT + "content/icon")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentIconResource
    implements AdminResource
{
    private ContentService contentService;

    private ContentImageHelper helper;

    private MediaInfoService mediaInfoService;

    private ImageService imageService;

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
            return cacheAndReturnResponse( timestamp, resolvedImage );
        }
        else
        {
            if ( content instanceof Media )
            {
                final Media media = (Media) content;
                if ( media.isImage() )
                {
                    resolvedImage = resolveResponseFromImageAttachment( media, size, crop );
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
                final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary );

                final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                    scaleSize( size ).
                    scaleSquare( crop ).
                    orientation( imageOrientation ).
                    build();

                final BufferedImage thumbnailImage = imageService.readImage( binary, readImageParams );
                return new ResolvedImage( thumbnailImage, contentThumbnail.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromImageAttachment( final Media media, final int size, final boolean crop )
    {
        final Attachment imageAttachment = media.getMediaAttachment();
        if ( imageAttachment != null )
        {
            final ByteSource attachmentBinary = contentService.getBinary( media.getId(), imageAttachment.getBinaryReference() );
            if ( attachmentBinary != null )
            {
                final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                    cropping( media.getCropping() ).
                    scaleSize( size ).
                    scaleSquare( crop ).
                    orientation( getSourceAttachmentOrientation( media ) ).
                    build();

                final BufferedImage contentImage = imageService.readImage( attachmentBinary, readImageParams );
                return new ResolvedImage( contentImage, imageAttachment.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    private ImageOrientation getSourceAttachmentOrientation( final Media media )
    {
        final ByteSource sourceBinary = contentService.getBinary( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return mediaInfoService.getImageOrientation( sourceBinary );
    }

    private Response cacheAndReturnResponse( final String timestamp, final ResolvedImage resolvedImage )
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

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentImageHelper( ContentImageHelper contentImageHelper )
    {
        this.helper = contentImageHelper;
    }

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }

    @Reference
    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }
}
