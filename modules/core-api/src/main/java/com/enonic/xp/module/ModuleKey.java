package com.enonic.xp.module;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class ModuleKey
{
    public final static ModuleKey SYSTEM = ModuleKey.from( "system" );

    public static final ModuleKey MEDIA_MOD = ModuleKey.from( "media" );

    public static final ModuleKey PORTAL = ModuleKey.from( "portal" );

    public static final ModuleKey BASE = ModuleKey.from( "base" );

    public static final ModuleKeys SYSTEM_RESERVED_MODULE_KEYS = ModuleKeys.from( SYSTEM, MEDIA_MOD, PORTAL, BASE );

    private final String name;

    private ModuleKey( final String name )
    {
        Preconditions.checkNotNull( name, "ModuleKey cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "ModuleKey cannot be blank" );
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleKey ) && ( (ModuleKey) o ).name.equals( this.name );
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

    public static ModuleKey from( final String name )
    {
        return new ModuleKey( name );
    }

    public static ModuleKey from( final Bundle bundle )
    {
        return ModuleKey.from( bundle.getSymbolicName() );
    }

    public static ModuleKey from( final Class<?> clzz )
    {
        return from( FrameworkUtil.getBundle( clzz ) );
    }
}
