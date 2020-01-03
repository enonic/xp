package com.enonic.xp.session;

import java.util.UUID;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SessionKey
{
    private final String value;

    private SessionKey( final String value )
    {
        this.value = value;
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

    public static SessionKey generate()
    {
        return new SessionKey( UUID.randomUUID().toString() );
    }
}
