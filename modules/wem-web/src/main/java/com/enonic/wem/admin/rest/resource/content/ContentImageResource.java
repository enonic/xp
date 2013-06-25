package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;
import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleSquareFilter;
import static com.enonic.wem.api.command.Commands.contentType;


@Path("content/image")
@Produces("image/*")
public class ContentImageResource
{
    private final ContentImageHelper helper;

    private Client client;

    public ContentImageResource()
    {
        this.helper = new ContentImageHelper();
    }

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentId,
                                     @QueryParam("size") @DefaultValue("128") final int size,
                                     @QueryParam("thumbnail") @DefaultValue("true") final boolean thumbnail )
        throws Exception
    {
        if ( contentId == null )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentIdValue = ContentId.from( contentId );
        final Content content = findContent( contentIdValue );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final QualifiedContentTypeName contentType = content.getType();

        if ( thumbnail )
        {
            // check if content has a thumbnail attachment ("_thumb.png")
            final Attachment attachmentThumbnail = findAttachment( contentIdValue, CreateContent.THUMBNAIL_NAME );
            if ( attachmentThumbnail != null )
            {
                final BufferedImage thumbnailImage = helper.getImageFromBinary( attachmentThumbnail.getBinary(), size, ScaleSquareFilter );
                return Response.ok( thumbnailImage, attachmentThumbnail.getMimeType() ).build();
            }
        }

        final String mimeType;
        final BufferedImage contentImage;
        if ( contentType.isImageMedia() )
        {
            final String attachmentName = getImageAttachmentName( content );
            final Attachment attachment = findAttachment( contentIdValue, attachmentName );

            final Binary binary = attachment.getBinary();
            if ( thumbnail )
            {
                contentImage = helper.getImageFromBinary( binary, size, ScaleSquareFilter );
            }
            else
            {
                contentImage = helper.getImageFromBinary( binary, size, ScaleMax );
            }
            mimeType = attachment.getMimeType();
        }
        else
        {
            final Icon contentTypeIcon = findRootContentTypeIcon( contentType );
            contentImage = helper.getIconImage( contentTypeIcon, size );
            mimeType = contentTypeIcon.getMimeType();
        }

        return Response.ok( contentImage, mimeType ).build();
    }

    private String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();

        final Property image = contentData.getProperty( "image" );
        return image == null ? content.getName() : image.getString();
    }

    private Icon findRootContentTypeIcon( final QualifiedContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType == null ? null : contentType.getIcon();
    }

    private ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentTypeName );
        return client.execute( contentType().get().qualifiedNames( qualifiedNames ) ).first();
    }

    private Content findContent( final ContentId contentId )
    {
        return client.execute( Commands.content().get().selectors( ContentIds.from( contentId ) ) ).first();
    }

    private Attachment findAttachment( final ContentId contentId, final String attachmentName )
    {
        return client.execute( Commands.attachment().get().contentSelector( contentId ).attachmentName( attachmentName ) );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
