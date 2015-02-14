package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.annotation.security.RolesAllowed;
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
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.security.RoleKeys;

import static com.enonic.xp.admin.impl.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMaxFilter;


@Path(ResourceConstants.REST_ROOT + "content/image")
@Produces("image/*")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ContentImageResource
    implements AdminResource
{
    private static final ContentImageHelper helper = new ContentImageHelper();

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentIdAsString, @QueryParam("size") final int size )
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
            resolvedImage = resolveResponseFromContentImageAttachment( (Media) content, size );
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


    private ResolvedImage resolveResponseFromContentImageAttachment( final Media media, final int size )
    {
        final Attachment attachment = media.getMediaAttachment();
        if ( attachment != null )
        {
            final ByteSource binary = contentService.getBinary( media.getId(), attachment.getBinaryReference() );
            if ( binary != null )
            {
                final BufferedImage contentImage = helper.readImage( binary, size, ScaleMaxFilter );
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
}
