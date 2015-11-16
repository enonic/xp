package com.enonic.xp.jaxrs.impl.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public final class MultipartFormReader
    implements MessageBodyReader<MultipartForm>
{
    private final MultipartService multipartService;

    public MultipartFormReader( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
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
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        return this.multipartService.parse( req );
    }
}
