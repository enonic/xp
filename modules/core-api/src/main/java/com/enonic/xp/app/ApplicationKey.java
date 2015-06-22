package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.module.ModuleKey;

@Beta
public final class ApplicationKey
{
    private final String name;

    private ApplicationKey( final String name )
    {
        Preconditions.checkNotNull( name, "ApplicationKey cannot be null" );
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ApplicationKey ) && ( (ApplicationKey) o ).name.equals( this.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static ApplicationKey from( final String name )
    {
        return new ApplicationKey( name );
    }

    public static ApplicationKey from( final ModuleKey module )
    {
        return ApplicationKey.from( module.getName() );
    }

}
