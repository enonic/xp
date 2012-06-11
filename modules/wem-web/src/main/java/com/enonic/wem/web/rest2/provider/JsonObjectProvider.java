package com.enonic.wem.web.rest2.provider;

import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.stereotype.Component;

@Component
@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
{
    public JsonObjectProvider()
    {
        setMapper( ObjectMapperFactory.create() );
    }
}
