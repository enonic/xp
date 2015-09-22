package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class ContentVersionId
{
    private final String value;

    private ContentVersionId( final String value )
    {
        Preconditions.checkNotNull( value );
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ContentVersionId ) && Objects.equals( this.value, ( (ContentVersionId) o ).value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public static ContentVersionId from( final String id )
    {
        return new ContentVersionId( id );
    }
}
