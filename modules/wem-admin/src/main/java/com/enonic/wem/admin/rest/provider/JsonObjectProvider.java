package com.enonic.wem.admin.rest.provider;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

@Provider
public final class JsonObjectProvider
    extends JacksonJsonProvider
    implements JaxRsComponent
{
    public JsonObjectProvider()
    {
        setMapper( ObjectMapperHelper.create() );
    }
}
