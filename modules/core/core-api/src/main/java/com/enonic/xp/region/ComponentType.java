package com.enonic.xp.region;

public abstract class ComponentType
{
    private final String shortName;

    public ComponentType( final String shortName )
    {
        this.shortName = shortName;
    }

    @Override
    public String toString()
    {
        return shortName;
    }
}
