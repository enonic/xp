package com.enonic.xp.session;

import static java.util.Objects.requireNonNull;


public final class SessionKey
{
    private final String value;

    private SessionKey( final String value )
    {
        this.value = requireNonNull( value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public boolean equals( final Object obj )
    {
        return ( obj instanceof SessionKey ) && ( (SessionKey) obj ).value.equals( this.value );
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public static SessionKey from( final String value )
    {
        return new SessionKey( value );
    }
}
