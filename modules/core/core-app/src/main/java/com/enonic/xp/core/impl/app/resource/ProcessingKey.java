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
    public int hashCode()
    {
        return Objects.hash( this.segment, this.key );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ProcessingKey ) && equals( (ProcessingKey) o );
    }

    private boolean equals( final ProcessingKey o )
    {
        return Objects.equals( this.segment, o.segment ) && Objects.equals( this.key, o.key );
    }
}
