package com.enonic.xp.query.highlight;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;

public final class HighlightQueryProperty
{
    private final String name;

    private final HighlightPropertySettings settings;

    private HighlightQueryProperty( final Builder builder )
    {
        this.name = builder.name;
        this.settings = builder.settings;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    private HighlightPropertySettings doGetSettings() {
        return settings;
    }

    public String getName()
    {
        return name;
    }

    public Fragmenter getFragmenter()
    {
        return doGetSettings().getFragmenter();
    }

    public Integer getFragmentSize()
    {
        return doGetSettings().getFragmentSize();
    }

    public Integer getNoMatchSize()
    {
        return doGetSettings().getNoMatchSize();
    }

    public Integer getNumOfFragments()
    {
        return doGetSettings().getNumOfFragments();
    }

    public Order getOrder()
    {
        return doGetSettings().getOrder();
    }

    public List<String> getPreTags()
    {
        return doGetSettings().getPreTags();
    }

    public List<String> getPostTags()
    {
        return doGetSettings().getPostTags();
    }

    public Boolean getRequireFieldMatch()
    {
        return doGetSettings().getRequireFieldMatch();
    }

    public static final class Builder
    {
        private final String name;

        private HighlightPropertySettings settings = HighlightPropertySettings.empty();

        private Builder( final String name )
        {
            this.name = name;
        }

        public Builder settings( final HighlightPropertySettings settings )
        {
            this.settings = settings;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name is required" );
        }

        public HighlightQueryProperty build()
        {
            validate();
            return new HighlightQueryProperty( this );
        }
    }
}
