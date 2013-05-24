package com.enonic.wem.admin.rest.provider;

import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.enonic.wem.admin.json.ObjectMapperHelper;


@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
{
    public JsonObjectProvider()
    {
        setMapper( ObjectMapperHelper.create() );
    }
}
