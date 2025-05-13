package com.enonic.xp.highlight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

public final class HighlightedProperties
    implements Iterable<HighlightedProperty>
{
    private final ImmutableMap<String, HighlightedProperty> highlightedFields;

    private HighlightedProperties( final Builder builder )
    {
        this.highlightedFields = ImmutableMap.copyOf( builder.highlightedFields );
    }

    public int size()
    {
        return highlightedFields.size();
    }

    public boolean isEmpty()
    {
        return highlightedFields.isEmpty();
    }

    public HighlightedProperty get( final String name )
    {
        return highlightedFields.get( name );
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
        final HighlightedProperties that = (HighlightedProperties) o;
        return Objects.equals( highlightedFields, that.highlightedFields );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( highlightedFields );
    }

    public static HighlightedProperties empty()
    {
        return HighlightedProperties.create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( HighlightedProperties source )
    {
        return new Builder( source );
    }

    @Override
    public Iterator<HighlightedProperty> iterator()
    {
        return highlightedFields.values().iterator();
    }

    public static final class Builder
    {
        final Map<String, HighlightedProperty> highlightedFields = new HashMap<>();

        private Builder()
        {
        }

        private Builder( final HighlightedProperties source )
        {
            if ( source == null )
            {
                return;
            }
            highlightedFields.putAll( source.highlightedFields );
        }

        public Builder add( final HighlightedProperty highlightedProperty )
        {
            highlightedFields.put( highlightedProperty.getName(), highlightedProperty );
            return this;
        }

        public HighlightedProperties build()
        {
            return new HighlightedProperties( this );
        }
    }

}
