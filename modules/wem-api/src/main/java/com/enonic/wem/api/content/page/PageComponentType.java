package com.enonic.wem.api.content.page;

public abstract class PageComponentType
{
    private final String shortName;
    
    private final Class componentClass;

    public PageComponentType( final String shortName, final Class componentClass )
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

    public abstract AbstractPageComponentDataSerializer getDataSerializer();
}
