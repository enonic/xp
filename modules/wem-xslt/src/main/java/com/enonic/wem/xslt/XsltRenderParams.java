package com.enonic.wem.xslt;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;

public final class XsltRenderParams
{
    private ResourceKey view;

    private String inputXml;

    private final Map<String, Object> parameters;

    public XsltRenderParams()
    {
        this.parameters = Maps.newHashMap();
    }

    public ResourceKey getView()
    {
        return this.view;
    }

    public String getInputXml()
    {
        return this.inputXml;
    }

    public Map<String, Object> getParameters()
    {
        return this.parameters;
    }

    public XsltRenderParams view( final ResourceKey view )
    {
        this.view = view;
        return this;
    }

    public XsltRenderParams inputXml( final String inputXml )
    {
        this.inputXml = inputXml;
        return this;
    }

    public XsltRenderParams parameter( final String name, final Object value )
    {
        this.parameters.put( name, value );
        return this;
    }

    public XsltRenderParams parameters( final Map<String, Object> parameters )
    {
        this.parameters.putAll( parameters );
        return this;
    }
}
