package com.enonic.xp.admin.impl.rest.provider;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.enonic.xp.admin.impl.json.ObjectMapperHelper;

@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
{
    public JsonObjectProvider()
    {
        setMapper( ObjectMapperHelper.create() );
    }
}
