package com.enonic.xp.content.page.region;

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

    public Class getComponentClass()
    {
        return componentClass;
    }
}
