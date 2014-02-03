package com.enonic.wem.web.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Charsets;

@Provider
public final class FreeMarkerViewWriter
    implements MessageBodyWriter<FreeMarkerView>
{
    @Inject
    protected FreeMarkerRenderer renderer;

    @Override
    public boolean isWriteable( final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType )
    {
        return FreeMarkerView.class.isAssignableFrom( type );
    }

    @Override
    public long getSize( final FreeMarkerView view, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo( final FreeMarkerView view, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        final String out = this.renderer.render( view );
        entityStream.write( out.getBytes( Charsets.UTF_8 ) );
    }
}
