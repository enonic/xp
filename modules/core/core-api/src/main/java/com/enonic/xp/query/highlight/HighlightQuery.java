package com.enonic.xp.query.highlight;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class HighlightQuery
{
    private final ImmutableSet<HighlightQueryField> fields;

    private final HighlightQuerySettings settings;

    private HighlightQuery( final Builder builder )
    {
        this.fields = ImmutableSet.copyOf( builder.fields );
        this.settings = builder.settings;
    }

    public static HighlightQuery empty()
    {
        return create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public HighlightQuerySettings getSettings()
    {
        return settings;
    }

    public ImmutableSet<HighlightQueryField> getFields()
    {
        return fields;
    }

    public static class Builder
    {
        private Set<HighlightQueryField> fields = Sets.newHashSet();

        private HighlightQuerySettings settings = HighlightQuerySettings.empty();

        public Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public Builder field( final HighlightQueryField value )
        {
            this.fields.add( value );
            return this;
        }

        public Builder settings( final HighlightQuerySettings settings )
        {
            this.settings = settings;
            return this;
        }

        public HighlightQuery build()
        {
            return new HighlightQuery( this );
        }

    }

}
