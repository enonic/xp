package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;

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

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Exceptions;


@Path(ResourceConstants.REST_ROOT + "content/icon")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class ContentIconResource
    implements JaxRsComponent
{
    private ContentService contentService;

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
            final ResolveIconParams params = new ResolveIconParams().
                setBinaryReference( contentThumbnail.getBinaryReference() ).
                setId( content.getId() ).
                setImageOrientation( getThumbnailOrientation( contentThumbnail, content.getId() ) ).
                setMimeType( contentThumbnail.getMimeType() ).
                setSize( size ).
                setCrop( crop );

            return this.resolveResponse( params );

        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromImageAttachment( final Media media, final int size, final boolean crop )
    {
        final Attachment imageAttachment = media.getMediaAttachment();

        if ( imageAttachment != null )
        {
            final ResolveIconParams params = new ResolveIconParams().
                setBinaryReference( imageAttachment.getBinaryReference() ).
                setId( media.getId() ).
                setImageOrientation( getSourceAttachmentOrientation( media ) ).
                setCropping( media.getCropping() ).
                setMimeType( imageAttachment.getMimeType() ).
                setFileName( imageAttachment.getName() ).
                setSize( size ).
                setCrop( crop );

            return this.resolveResponse( params );
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponse( final ResolveIconParams params )
    {
        try
        {
            final boolean isSVG = params.mimeType.equals( "image/svg+xml" );

            if ( isSVG )
            {
                final ByteSource binary = contentService.getBinary( params.id, params.binaryReference );
                return new ResolvedImage( binary.read(), params.mimeType, params.fileName );
            }
            else
            {
                final String format = imageService.getFormatByMimeType( params.mimeType );

                final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                    contentId( params.id ).
                    binaryReference( params.binaryReference ).
                    cropping( params.cropping ).
                    scaleSize( params.size ).
                    scaleSquare( params.crop ).
                    format( format ).
                    orientation( params.imageOrientation ).
                    build();

                final ByteSource contentImage = imageService.readImage( readImageParams );
                return new ResolvedImage( contentImage.read(), params.mimeType );
            }

        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private static class ResolveIconParams
    {
        private ContentId id;

        private BinaryReference binaryReference;

        private ImageOrientation imageOrientation;

        private Cropping cropping;

        private String mimeType;

        private Integer size;

        private Boolean crop;

        private String fileName;

        public ResolveIconParams setId( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public ResolveIconParams setBinaryReference( final BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
            return this;
        }

        public ResolveIconParams setImageOrientation( final ImageOrientation imageOrientation )
        {
            this.imageOrientation = imageOrientation;
            return this;
        }

        public ResolveIconParams setCropping( final Cropping cropping )
        {
            this.cropping = cropping;
            return this;
        }

        public ResolveIconParams setMimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public ResolveIconParams setSize( final Integer size )
        {
            this.size = size;
            return this;
        }

        public ResolveIconParams setCrop( final Boolean crop )
        {
            this.crop = crop;
            return this;
        }

        public ResolveIconParams setFileName( final String fileName )
        {
            this.fileName = fileName;
            return this;
        }
    }

    private ImageOrientation getSourceAttachmentOrientation( final Media media )
    {
        final ByteSource sourceBinary = contentService.getBinary( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return mediaInfoService.getImageOrientation( sourceBinary, media );
    }

    private ImageOrientation getThumbnailOrientation( final Thumbnail thumbnail, final ContentId id )
    {
        final ByteSource sourceBinary = contentService.getBinary( id, thumbnail.getBinaryReference() );
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
