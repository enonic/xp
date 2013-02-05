package com.enonic.wem.core.support.dao;


import com.google.common.base.Preconditions;

public abstract class AbstractId
{
    private final String value;

    protected AbstractId( final String value )
    {
        Preconditions.checkNotNull( value, "No use of an id with a null-value" );
        this.value = value;
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

        final AbstractId that = (AbstractId) o;

        if ( !value.equals( that.value ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public String toString()
    {
        return value;
    }
}
