package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.Media;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.Exceptions;


@Path(ResourceConstants.REST_ROOT + "content/image")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentImageResource
    implements JaxRsComponent
{
    private static final ContentImageHelper HELPER = new ContentImageHelper();

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private MediaInfoService mediaInfoService;

    private ImageService imageService;

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentIdAsString, @QueryParam("size") final int size,
                                     @QueryParam("scaleWidth") @DefaultValue("false") final boolean scaleWidth,
                                     @QueryParam("source") @DefaultValue("false") final boolean source,
                                     @QueryParam("scale") final String scale )
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

        ResolvedImage resolvedImage;

        if ( content instanceof Media )
        {
            if ( content.getType().isVectorMedia() )
            {
                resolvedImage = resolveResponseFromContentSVGAttachment( (Media) content );
            }
            else
            {
                resolvedImage = resolveResponseFromContentImageAttachment( (Media) content, size, scaleWidth, source, scale );
            }
            if ( resolvedImage.isOK() )
            {
                final CacheControl cacheControl = new CacheControl();
                cacheControl.setMaxAge( Integer.MAX_VALUE );
                return resolvedImage.toResponse( cacheControl );
            }
        }

        resolvedImage = resolveResponseFromContentType( content, size );
        if ( resolvedImage.isOK() )
        {
            return resolvedImage.toResponse();
        }
        else
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
    }

    private ResolvedImage resolveResponseFromContentSVGAttachment( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                try
                {
                    return new ResolvedImage( binary.read(), attachment.getMimeType() );
                }
                catch ( final IOException e )
                {
                    throw Exceptions.unchecked( e );
                }
            }
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromContentImageAttachment( final Media media, final int size, final boolean scaleWidth,
                                                                     final boolean source, final String scale )
    {
        final Attachment attachment = media.getMediaAttachment();
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                try
                {
                    final Cropping cropping = source ? null : media.getCropping();
                    final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary );
                    final String format = imageService.getFormatByMimeType( attachment.getMimeType() );
                    final ScaleParams scaleParams = parseScaleParam( media, scale, size );

                    final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                        contentId( media.getId() ).
                        binaryReference( attachment.getBinaryReference() ).
                        cropping( cropping ).
                        scaleParams( scaleParams ).
                        focalPoint( scaleParams == null ? new FocalPoint( 0, 0 ) : media.getFocalPoint() ).
                        scaleSize( size ).
                        scaleWidth( scaleWidth ).
                        format( format ).
                        orientation( imageOrientation ).
                        build();

                    final ByteSource contentImage = imageService.readImage( readImageParams );
                    return new ResolvedImage( contentImage.read(), attachment.getMimeType() );
                }
                catch ( IOException e )
                {
                    throw Exceptions.unchecked( e );
                }
            }
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromContentType( final Content content, final int size )
    {
        final ContentType superContentTypeWithIcon = resolveSuperContentTypeWithIcon( content.getType() );
        if ( superContentTypeWithIcon == null || superContentTypeWithIcon.getIcon() == null )
        {
            return ResolvedImage.unresolved();
        }

        final BufferedImage contentImage = HELPER.resizeImage( superContentTypeWithIcon.getIcon().asInputStream(), size );
        final String mimeType = superContentTypeWithIcon.getIcon().getMimeType();

        return new ResolvedImage( contentImage, mimeType );
    }

    private ContentType resolveSuperContentTypeWithIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }

    private ScaleParams parseScaleParam( final Media media, final String scale, final int size )
    {
        if ( scale != null )
        {
            final int pos = scale.indexOf( ":" );
            final String horizontalProportion = scale.substring( 0, pos );
            final String verticalProportion = scale.substring( pos + 1 );

            final int width = size > 0 ? size : getOriginalWidth( media );
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return new ScaleParams( "block", new Object[]{width, height} );
        }

        return null;
    }

    private int getOriginalWidth( final Media media )
    {
        ExtraData imageData = media.getAllExtraData().getMetadata( MixinName.from( "media:imageInfo" ) );
        if ( imageData != null )
        {
            return imageData.getData().getProperty( "imageWidth" ).getValue().asLong().intValue();
        }

        return 0;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
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
