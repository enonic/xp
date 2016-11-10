package com.enonic.xp.app;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.CharacterChecker;

@Beta
public final class ApplicationKey
{
    public final static ApplicationKey SYSTEM = ApplicationKey.from( "system" );

    public static final ApplicationKey MEDIA_MOD = ApplicationKey.from( "media" );

    public static final ApplicationKey PORTAL = ApplicationKey.from( "portal" );

    public static final ApplicationKey BASE = ApplicationKey.from( "base" );

    public static final ApplicationKeys SYSTEM_RESERVED_APPLICATION_KEYS = ApplicationKeys.from( SYSTEM, MEDIA_MOD, PORTAL, BASE );

    private final String name;

    private ApplicationKey( final String name )
    {
        Preconditions.checkNotNull( name, "ApplicationKey cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "ApplicationKey cannot be blank" );
        this.name = CharacterChecker.check( name, "Not a valid ApplicationKey [" + name + "]" );
    }

    public String getName()
    {
        return name;
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

    public static ApplicationKey from( final Bundle bundle )
    {
        return ApplicationKey.from( bundle.getSymbolicName() );
    }

    public static ApplicationKey from( final Class<?> clzz )
    {
        return from( FrameworkUtil.getBundle( clzz ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ApplicationKey ) && this.name.equals( ( (ApplicationKey) o ).name );
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
}
