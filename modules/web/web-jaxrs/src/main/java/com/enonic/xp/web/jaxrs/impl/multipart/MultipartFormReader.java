package com.enonic.xp.web.jaxrs.impl.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class MultipartFormReader
    implements MessageBodyReader<MultipartForm>
{
    private MultipartService multipartService;

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
        return this.multipartService.parse( entityStream, mediaType.toString() );
    }

    @Reference
    public void setMultipartService( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
    }
}
