package com.enonic.wem.web.rest.provider;

import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.json.ObjectMapperHelper;

@Component
@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
{
    public JsonObjectProvider()
    {
        setMapper( ObjectMapperHelper.create() );
    }
}
