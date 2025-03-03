package com.enonic.xp.jaxrs.impl.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class MultipartFormReader
    implements MessageBodyReader<MultipartForm>
{
    private final MultipartService multipartService;

    private HttpServletRequest request;

    public MultipartFormReader( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
    }

    @Context
    public void setHttpServletRequest( final HttpServletRequest request )
    {
        this.request = request;
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
        return this.multipartService.parse( request );
    }
}
