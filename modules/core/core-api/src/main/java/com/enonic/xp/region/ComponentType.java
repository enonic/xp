package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
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
