package com.enonic.xp.page.region;

import com.google.common.annotations.Beta;

@Beta
public abstract class ComponentType
{
    private final String shortName;

    private final Class componentClass;

    public ComponentType( final String shortName, final Class componentClass )
    {
        this.shortName = shortName;
        this.componentClass = componentClass;
    }

    @Override
    public String toString()
    {
        return shortName;
    }

    public Class getComponentClass()
    {
        return componentClass;
    }
}
