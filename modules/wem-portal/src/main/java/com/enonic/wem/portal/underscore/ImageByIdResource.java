package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;

public final class ImageByIdResource
    extends ImageBaseResource
{
    @Override
    protected Representation get()
        throws ResourceException
    {
        final ContentId imageContentId = ContentId.from( getAttribute( "id" ) );
        final Content imageContent = getContent( imageContentId );

        final Attachment attachment = getAttachment( imageContent.getId() );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found", imageContent.getName().toString() );
        }

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw notFound( "Blob [%s] not found", attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );
        final String format = getFormat( attachment.getName() );
        final BufferedImage image = applyFilters( contentImage, format );

        final MediaType mediaType = MediaType.valueOf( attachment.getMimeType() );
        final byte[] imageData = serializeImage( image, format );
        return new ByteArrayRepresentation( imageData, mediaType );
    }
}
