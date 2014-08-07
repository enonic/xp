package com.enonic.wem.portal.view;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ModuleResourceKey;

public final class RenderViewSpec
{
    private ModuleResourceKey view;

    private final Map<String, Object> params;

    private String processor;

    public RenderViewSpec()
    {
        this.params = Maps.newHashMap();
    }

    public RenderViewSpec view( final String view )
    {
        return view( ModuleResourceKey.from( view ) );
    }

    public RenderViewSpec view( final ModuleResourceKey view )
    {
        this.view = view;
        return this;
    }

    public RenderViewSpec param( final String key, final Object value )
    {
        this.params.put( key, value );
        return this;
    }

    public RenderViewSpec params( final Map<String, Object> params )
    {
        this.params.putAll( params );
        return this;
    }

    public RenderViewSpec processor( final String processor )
    {
        this.processor = processor;
        return this;
    }

    public ModuleResourceKey getView()
    {
        return this.view;
    }

    public Map<String, Object> getParams()
    {
        return this.params;
    }

    public String getProcessor()
    {
        return this.processor;
    }
}
