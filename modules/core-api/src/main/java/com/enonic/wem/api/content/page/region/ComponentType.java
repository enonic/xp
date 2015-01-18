package com.enonic.wem.api.content.page.region;

import com.enonic.wem.api.content.page.ComponentDataSerializer;

public abstract class ComponentType
{
    private final String shortName;
    
    private final Class componentClass;

    public ComponentType( final String shortName, final Class componentClass )
    {
        this.shortName = shortName;
        this.componentClass = componentClass;
    }

    public String toString()
    {
        return shortName;
    }

    Class getComponentClass()
    {
        return componentClass;
    }

    public abstract ComponentDataSerializer getDataSerializer();
}
