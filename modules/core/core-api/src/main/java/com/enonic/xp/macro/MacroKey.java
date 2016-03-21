package com.enonic.xp.macro;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.CharacterChecker;

public class MacroKey
{
    protected static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private final String refString;

    public MacroKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = applicationKey;
        this.name = CharacterChecker.check( name, "Not a valid name for MacroKey [" + name + "]" );
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
        final String macrosName = StringUtils.substringAfter( s, SEPARATOR );
        return new MacroKey( ApplicationKey.from( applicationKey ), macrosName );
    }

    public static MacroKey from( final ApplicationKey applicationKey, final String macrosName )
    {
        return new MacroKey( applicationKey, macrosName );
    }
}
