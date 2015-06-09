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
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.RoleKeys;


@Path(ResourceConstants.REST_ROOT + "content/image")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentImageResource
    implements AdminResource
{
    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private ContentImageHelper helper;

    private MediaInfoService mediaInfoService;

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentIdAsString, @QueryParam("size") final int size,
                                     @QueryParam("scaleWidth") @DefaultValue("false") final boolean scaleWidth )
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
            resolvedImage = resolveResponseFromContentImageAttachment( (Media) content, size, scaleWidth );
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


    private ResolvedImage resolveResponseFromContentImageAttachment( final Media media, final int size, final boolean scaleWidth )
    {
        final Attachment attachment = media.getMediaAttachment();
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary );
                final ImageParams imageParams = ImageParams.newImageParams().
                    size( size ).
                    orientation( imageOrientation ).
                    scaleWidth( scaleWidth ).
                    build();

                final BufferedImage contentImage = helper.readAndRotateImage( binary, imageParams );
                return new ResolvedImage( contentImage, attachment.getMimeType() );
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

        final BufferedImage contentImage = helper.resizeImage( superContentTypeWithIcon.getIcon().asInputStream(), size );
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
    public void setContentImageHelper( ContentImageHelper contentImageHelper )
    {
        this.helper = contentImageHelper;
    }

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }
}
