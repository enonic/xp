package com.enonic.wem.api.item;


import java.util.UUID;

public class ItemId
{
    private final String value;

    public ItemId()
    {
        this.value = UUID.randomUUID().toString();
    }

    public ItemId( final String value )
    {
        this.value = value;
    }

    public ItemId( final Object value )
    {
        this.value = value.toString();
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

        final ItemId itemId = (ItemId) o;

        return value.equals( itemId.value );
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
