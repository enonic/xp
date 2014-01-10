package com.enonic.wem.api.content.page;

public final class ComponentName
{
    private final String value;

    public ComponentName( final String value )
    {
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ComponentName ) && ( (ComponentName) o ).value.equals( this.value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public String toString()
    {
        return value;
    }
}
