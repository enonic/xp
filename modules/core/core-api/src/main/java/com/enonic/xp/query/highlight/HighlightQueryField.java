package com.enonic.xp.query.highlight;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;

public class HighlightQueryField
{
    private final String name;

    private final HighlightFieldSettings settings;

    private HighlightQueryField( final Builder builder )
    {
        this.name = builder.name;
        this.settings = builder.settings;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    private HighlightFieldSettings doGetSettings() {
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

    public ImmutableList<String> getPreTags()
    {
        return doGetSettings().getPreTags();
    }

    public ImmutableList<String> getPostTags()
    {
        return doGetSettings().getPostTags();
    }

    public Boolean getRequireFieldMatch()
    {
        return doGetSettings().getRequireFieldMatch();
    }

    public static class Builder
    {
        private String name;

        private HighlightFieldSettings settings = HighlightFieldSettings.empty();

        public Builder( final String name )
        {
            this.name = name;
        }

        public Builder settings( final HighlightFieldSettings settings )
        {
            this.settings = settings;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name is required" );
        }

        public HighlightQueryField build()
        {
            validate();
            return new HighlightQueryField( this );
        }
    }
}
