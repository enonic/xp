package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;

public final class ImageByNameResource
    extends ImageResource
{
    @Override
    protected Representation get()
        throws ResourceException
    {
        final ContentPath path = ContentPath.from( this.contentPath );
        final Content content = getContent( path );

        final String fileName = getAttribute( "fileName" );
        final Attachment attachment = getAttachment( content.getId(), fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw notFound( "Blob [%s] not found", attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );
        final String format = getFormat( fileName );

        final BufferedImage image = applyFilters( contentImage, format );

        final MediaType mediaType = MediaType.valueOf( attachment.getMimeType() );
        final byte[] imageData = serializeImage( image, format );
        return new ByteArrayRepresentation( imageData, mediaType );
    }
}
