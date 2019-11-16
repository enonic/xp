package com.enonic.xp.macro;

import java.util.Objects;

import com.enonic.xp.migration.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.CharacterChecker;

@Beta
public final class MacroKey
{
    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private final String refString;

    private MacroKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = applicationKey;
        this.name = CharacterChecker.check( name, "Not a valid Macro name [" + name + "]" );
        this.refString = applicationKey.toString() + SEPARATOR + name;
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
        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static MacroKey from( final String s )
    {
        final String applicationKey = StringUtils.substringBefore( s, SEPARATOR );
        final String macroName = StringUtils.substringAfter( s, SEPARATOR );
        return new MacroKey( ApplicationKey.from( applicationKey ), macroName );
    }

    public static MacroKey from( final ApplicationKey applicationKey, final String macroName )
    {
        return new MacroKey( applicationKey, macroName );
    }
}
