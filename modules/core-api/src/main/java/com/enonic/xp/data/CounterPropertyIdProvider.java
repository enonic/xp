package com.enonic.xp.data;

public final class CounterPropertyIdProvider
    implements PropertyIdProvider
{
    private int nextId = 1;

    @Override
    public PropertyId nextId()
    {
        return new PropertyId( String.valueOf( nextId++ ) );
    }
}
