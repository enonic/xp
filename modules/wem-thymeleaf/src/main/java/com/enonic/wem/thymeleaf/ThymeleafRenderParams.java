package com.enonic.wem.thymeleaf;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;

public final class ThymeleafRenderParams
{
    private ResourceKey view;

    private final Map<String, Object> parameters;

    public ThymeleafRenderParams()
    {
        this.parameters = Maps.newHashMap();
    }

    public ResourceKey getView()
    {
        return this.view;
    }

    public Map<String, Object> getParameters()
    {
        return this.parameters;
    }

    public ThymeleafRenderParams view( final ResourceKey view )
    {
        this.view = view;
        return this;
    }

    public ThymeleafRenderParams parameter( final String name, final Object value )
    {
        return this;
    }

    public ThymeleafRenderParams parameters( final String name, final Object value )
    {
        this.parameters.put( name, value );
        return this;
    }

    public ThymeleafRenderParams parameters( final Map<String, Object> parameters )
    {
        this.parameters.putAll( parameters );
        return this;
    }
}
