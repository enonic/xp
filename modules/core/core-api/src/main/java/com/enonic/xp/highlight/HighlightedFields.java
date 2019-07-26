package com.enonic.xp.highlight;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;

public class HighlightedFields
{
    private final Map<String, HighlightedField> highlightedFields;

    private HighlightedFields( final Builder builder )
    {
        this.highlightedFields = builder.highlightedFields;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( HighlightedFields source )
    {
        return new Builder( source );
    }

    public static HighlightedFields empty()
    {
        return HighlightedFields.create().build();
    }

    public Object get( final String key )
    {
        return highlightedFields.get( key );
    }

    public Map<String, HighlightedField> getHighlightedFields()
    {
        return highlightedFields;
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
        final HighlightedFields that = (HighlightedFields) o;
        return Objects.equals( highlightedFields, that.highlightedFields );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( highlightedFields );
    }

    public static final class Builder
    {
        final Map<String, HighlightedField> highlightedFields = Maps.newHashMap();

        private Builder()
        {
        }

        private Builder( final HighlightedFields source )
        {
            if ( source == null )
            {
                return;
            }
            highlightedFields.putAll( source.highlightedFields );
        }

        public Builder add( final String key, final HighlightedField value )
        {
            highlightedFields.put( key, value );
            return this;
        }

        public HighlightedFields build()
        {
            return new HighlightedFields( this );
        }
    }

}
