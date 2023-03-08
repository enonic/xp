package com.enonic.xp.core.impl.app.resource;

import java.util.Objects;

final class ProcessingKey
{
    private final String segment;

    private final Object key;

    ProcessingKey( final String segment, final Object key )
    {
        this.segment = segment;
        this.key = key;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ProcessingKey ) )
        {
            return false;
        }
        final ProcessingKey that = (ProcessingKey) o;
        return Objects.equals( this.segment, that.segment ) && Objects.equals( this.key, that.key );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.segment, this.key );
    }
}
