package com.enonic.xp.query.highlight;

import com.google.common.collect.ImmutableSet;

public class HighlightQuery
{
    private final ImmutableSet<HighlightQueryProperty> properties;

    private final HighlightQuerySettings settings;

    private HighlightQuery( final Builder builder )
    {
        this.properties = builder.properties.build();
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

    public ImmutableSet<HighlightQueryProperty> getProperties()
    {
        return properties;
    }

    public static class Builder
    {
        private ImmutableSet.Builder<HighlightQueryProperty> properties = ImmutableSet.builder();

        private HighlightQuerySettings settings = HighlightQuerySettings.empty();

        public Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public Builder property( final HighlightQueryProperty value )
        {
            this.properties.add( value );
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
