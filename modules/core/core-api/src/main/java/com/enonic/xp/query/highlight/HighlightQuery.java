package com.enonic.xp.query.highlight;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class HighlightQuery
{
    private final ImmutableList<HighlightQueryProperty> properties;

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

    public List<HighlightQueryProperty> getProperties()
    {
        return properties;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<HighlightQueryProperty> properties = ImmutableList.builder();

        private HighlightQuerySettings settings = HighlightQuerySettings.empty();

        private Builder()
        {
        }

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
