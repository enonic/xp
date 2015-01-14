package com.enonic.wem.admin.rest.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class MultipartFormReader
    implements MessageBodyReader<MultipartForm>
{
    private final FileUpload fileUpload;

    public MultipartFormReader()
    {
        this.fileUpload = new FileUpload();
        this.fileUpload.setFileItemFactory( new DiskFileItemFactory() );
    }

    @Override
    public boolean isReadable( final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType )
    {
        return mediaType.isCompatible( MediaType.MULTIPART_FORM_DATA_TYPE ) && ( type == MultipartForm.class );
    }

    @Override
    public MultipartForm readFrom( final Class<MultipartForm> type, final Type genericType, final Annotation[] annotations,
                                   final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
                                   final InputStream entityStream )
        throws IOException, WebApplicationException
    {
        final MultipartFormContext context = new MultipartFormContext( entityStream, mediaType );
        final List<FileItem> items = parseRequest( context );
        return new MultipartFormImpl( items );
    }

    private List<FileItem> parseRequest( final RequestContext context )
        throws IOException
    {
        try
        {
            return this.fileUpload.parseRequest( context );
        }
        catch ( final Exception e )
        {
            throw new IOException( e );
        }
    }
}
