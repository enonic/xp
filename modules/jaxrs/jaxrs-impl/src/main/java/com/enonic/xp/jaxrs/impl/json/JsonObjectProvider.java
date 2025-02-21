package com.enonic.xp.jaxrs.impl.json;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.enonic.xp.core.internal.ObjectMapperHelper;

@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
{
    public JsonObjectProvider()
    {
        super( ObjectMapperHelper.create() );
    }
}
