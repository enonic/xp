package com.enonic.xp.name;

import java.util.Arrays;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class Name
{
    private final String value;

    public Name( final String name )
    {
        doValidateName( name );

        this.value = name;
    }

    protected void doValidateName( final String name )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkArgument( !name.trim().isEmpty(), "name cannot be empty" );
        Preconditions.checkArgument( NameCharacterHelper.hasNoExplicitIllegal( name ), "Invalid name: '" + name + "'. Cannot contain " +
            Arrays.toString( NameCharacterHelper.getExplicitlyIllegalCharacters() ) );
        checkValidName( name );
    }

    protected boolean checkValidName( final String value )
    {

        for ( final char c : value.toCharArray() )
        {
            if ( !NameCharacterHelper.isValidCharacter( c ) )
            {
                throw new IllegalArgumentException(
                    "Invalid character in name : " + c + " (" + NameCharacterHelper.getUnicodeString( c ) + ")" );
            }
        }

        return true;
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

        final Name name = (Name) o;

        if ( value != null ? !value.equals( name.value ) : name.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static Name from( final String name )
    {
        return new Name( name );
    }
}
