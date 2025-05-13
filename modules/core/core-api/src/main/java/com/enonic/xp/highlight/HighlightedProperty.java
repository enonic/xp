package com.enonic.xp.highlight;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;


public final class HighlightedProperty
{
    private final String name;

    private final ImmutableSet<String> fragments;

    private HighlightedProperty( final Builder builder )
    {
        this.name = builder.name;
        this.fragments = ImmutableSet.copyOf( builder.fragments );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return name;
    }

    public Set<String> getFragments()
    {
        return fragments;
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
        final HighlightedProperty that = (HighlightedProperty) o;
        return Objects.equals( name, that.name ) && Objects.equals( fragments, that.fragments );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, fragments );
    }

    public static final class Builder
    {
        private String name;

        private final Set<String> fragments = new HashSet<>();

        private Builder()
        {
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder addFragment( final String fragment )
        {
            this.fragments.add( fragment );
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set" );
        }

        public HighlightedProperty build()
        {
            this.validate();
            return new HighlightedProperty( this );
        }
    }
}
