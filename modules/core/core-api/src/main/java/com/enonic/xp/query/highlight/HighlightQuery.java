package com.enonic.xp.query.highlight;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class HighlightQuery
{
    private final ImmutableSet<HighlightQueryField> fields;

    private HighlightQuery( final Builder builder )
    {
        this.fields = ImmutableSet.copyOf( builder.fields );
    }

    public static HighlightQuery empty()
    {
        return create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<HighlightQueryField> getFields()
    {
        return fields;
    }

    public static class Builder
    {
        private Set<HighlightQueryField> fields = Sets.newHashSet();

        public Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public Builder field( final HighlightQueryField value )
        {
            this.fields.add( value );
            return this;
        }

        public HighlightQuery build()
        {
            return new HighlightQuery( this );
        }

    }
}
