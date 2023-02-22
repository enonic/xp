package com.enonic.xp.macro;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.CharacterChecker;

@PublicApi
public final class MacroKey
{
    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private MacroKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = Objects.requireNonNull( applicationKey );
        this.name = CharacterChecker.check( name, "Not a valid Macro name [" + name + "]" );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final MacroKey that = (MacroKey) o;
        return applicationKey.equals( that.applicationKey ) && name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, name );
    }
    @Override
    public String toString()
    {
        return applicationKey + SEPARATOR + name;
    }

    public static MacroKey from( final String key )
    {
        Preconditions.checkNotNull( key, "MacroKey can't be null" );
        final int index = key.indexOf( SEPARATOR );
        final String applicationKey = index == -1 ? key : key.substring( 0, index );
        final String macroName = index == -1 ? "" : key.substring( index + 1 );
        return new MacroKey( ApplicationKey.from( applicationKey ), macroName );
    }

    public static MacroKey from( final ApplicationKey applicationKey, final String macroName )
    {
        return new MacroKey( applicationKey, macroName );
    }
}
