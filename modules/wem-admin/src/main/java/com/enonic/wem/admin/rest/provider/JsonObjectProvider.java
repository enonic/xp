package com.enonic.wem.admin.rest.provider;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

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
