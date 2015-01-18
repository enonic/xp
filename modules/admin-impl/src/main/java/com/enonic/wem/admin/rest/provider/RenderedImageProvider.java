package com.enonic.wem.admin.rest.provider;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("image/*")
@Consumes({"image/*", "application/octet-stream"})
public final class RenderedImageProvider
    implements MessageBodyWriter<RenderedImage>
{
    @Override
    public boolean isWriteable( final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType )
    {
        return RenderedImage.class.isAssignableFrom( type );
    }

    @Override
    public long getSize( final RenderedImage renderedImage, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo( final RenderedImage renderedImage, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        String formatName = getWriterFormatName( mediaType );
        if ( formatName == null )
        {
            throw new IOException( "The image-based media type " + mediaType + " is not supported for writing" );
        }

        ImageIO.write( renderedImage, formatName, entityStream );
    }

    private String getWriterFormatName( final MediaType t )
    {
        return getWriterFormatName( t.toString() );
    }

    private String getWriterFormatName( final String t )
    {
        final Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType( t );
        if ( !i.hasNext() )
        {
            return null;
        }

        return i.next().getOriginatingProvider().getFormatNames()[0];
    }
}
