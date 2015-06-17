package com.enonic.xp.module;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class ModuleKey
{
    private static final String SYSTEM_MODULE_NAME = "system";

    public final static ModuleKey SYSTEM = new ModuleKey( SYSTEM_MODULE_NAME );

    public static final ModuleKey MEDIA_MOD = ModuleKey.from( "media" );

    public static final ModuleKey PORTAL = ModuleKey.from( "portal" );

    public static final ModuleKey BASE = ModuleKey.from( "base" );

    private final String name;

    private ModuleKey( final String name )
    {
        Preconditions.checkNotNull( name, "ModuleKey cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "ModuleKey cannot be blank" );
        this.name = name;
    }

    public static boolean isSystemReservedModuleKey( final ModuleKey moduleKey )
    {
        return moduleKey.equals( MEDIA_MOD ) || moduleKey.equals( PORTAL ) || moduleKey.equals( BASE );
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
