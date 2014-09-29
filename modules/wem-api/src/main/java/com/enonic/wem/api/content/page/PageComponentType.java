package com.enonic.wem.api.content.page;

public abstract class PageComponentType
{
    private final String shortName;

    public PageComponentType( final String shortName, final Class clazz )
    {
        this.shortName = shortName;
    }

    public String toString()
    {
        return shortName;
    }

    public abstract AbstractPageComponentDataSerializer getDataSerializer();
}
