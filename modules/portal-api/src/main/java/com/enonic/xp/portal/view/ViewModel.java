package com.enonic.xp.portal.view;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;

public final class ViewModel
{
    private String name;

    private ResourceKey view;

    private Map<String, Object> model;

    public ViewModel()
    {
        this.model = Maps.newHashMap();
    }

    public String getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public ResourceKey getView()
    {
        return this.view;
    }

    public void setView( final ResourceKey view )
    {
        this.view = view;
    }

    public Map<String, Object> getModel()
    {
        return this.model;
    }

    public void setModel( final Map<String, Object> model )
    {
        this.model = model;
    }
}
