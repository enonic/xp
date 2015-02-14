package com.enonic.xp.content.page.region;

import java.util.Objects;


public final class ComponentName
{
    private final String value;

    public ComponentName( final String value )
    {
        if ( value == null )
        {
            System.out.println( "ComponentName.value is null" );
            throw new NullPointerException( "ContentName value cannot be null" );
        }
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ComponentName ) )
        {
            return false;
        }

        final ComponentName that = (ComponentName) o;

        return Objects.equals( this.value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.value );
    }

    public String toString()
    {
        return value;
    }

    public static ComponentName from( final String value )
    {
        return new ComponentName( value );
    }
}
