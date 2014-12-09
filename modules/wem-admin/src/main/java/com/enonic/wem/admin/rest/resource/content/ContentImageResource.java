package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;


@Path(ResourceConstants.REST_ROOT + "content/image")
@Produces("image/*")
public final class ContentImageResource
    implements JaxRsComponent
{
    private static final ContentImageHelper helper = new ContentImageHelper();

    private ContentTypeService contentTypeService;

    private BlobService blobService;

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

        if ( content.getType().isImageMedia() )
        {
            resolvedImage = resolveResponseFromContentImageAttachment( content, size );
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


    private ResolvedImage resolveResponseFromContentImageAttachment( final Content content, final int size )
    {
        final String attachmentName = getImageAttachmentName( content );
        final Attachment attachment = content.getAttachments().getAttachment( attachmentName );

        if ( attachment != null )
        {
            final Blob blob = blobService.get( attachment.getBlobKey() );
            if ( blob != null )
            {
                final BufferedImage contentImage = helper.getImageFromBlob( blob, size, ScaleMax );
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

    private String getImageAttachmentName( final Content content )
    {
        final PropertyTree contentData = content.getData();

        final String image = contentData.getString( "image" );
        return image == null ? content.getName().toString() : image;
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

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
