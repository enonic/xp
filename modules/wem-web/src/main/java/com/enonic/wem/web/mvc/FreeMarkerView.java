package com.enonic.wem.web.mvc;

import java.util.Map;

import com.google.common.collect.Maps;

public final class FreeMarkerView
{
    private final String template;

    private final Map<String, Object> model;

    private FreeMarkerView( final String template )
    {
        this.template = template;
        this.model = Maps.newHashMap();
    }

    public String getTemplate()
    {
        return this.template;
    }

    public Map<String, Object> getModel()
    {
        return this.model;
    }

    public FreeMarkerView put( final String name, final Object value )
    {
        this.model.put( name, value );
        return this;
    }

    public static FreeMarkerView template( final String name )
    {
        return new FreeMarkerView( name );
    }
}
