package com.enonic.wem.web.rest2.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest2.common.JsonResult;

@Component
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class JsonResultProvider
    implements MessageBodyWriter<JsonResult>
{
    private final ObjectMapper mapper;

    public JsonResultProvider()
    {
        this.mapper = ObjectMapperFactory.create();
    }

    @Override
    public boolean isWriteable( final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType )
    {
        return true;
    }

    @Override
    public long getSize( final JsonResult o, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo( final JsonResult o, final Class<?> type, final Type genericType, final Annotation[] annotations,
                         final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        final JsonNode node = o.toJson();
        this.mapper.writeValue( entityStream, node );
    }
}
