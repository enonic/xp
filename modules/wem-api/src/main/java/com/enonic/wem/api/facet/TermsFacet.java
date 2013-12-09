package com.enonic.wem.api.facet;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class TermsFacet
    extends AbstractFacet
    implements Facet
{
    private final long total;

    private final long missing;

    private final long other;

    private final ImmutableSet<TermsFacetEntry> entries;

    private TermsFacet( final Builder builder )
    {
        super( builder.name );

        this.total = builder.total;
        this.missing = builder.missing;
        this.other = builder.other;
        this.entries = ImmutableSet.copyOf( builder.entries );
    }

    public long getTotal()
    {
        return total;
    }

    public long getMissing()
    {
        return missing;
    }

    public long getOther()
    {
        return other;
    }

    public ImmutableSet<TermsFacetEntry> getEntries()
    {
        return entries;
    }

    public static Builder newTermsFacet( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractFacet.Builder
    {
        private long total;

        private long missing;

        private long other;

        private Set<TermsFacetEntry> entries = Sets.newLinkedHashSet();

        public Builder( final String name )
        {
            super( name );
        }

        public Builder total( final long total )
        {
            this.total = total;
            return this;
        }

        public Builder missing( final long missing )
        {
            this.missing = missing;
            return this;
        }

        public Builder other( final long other )
        {
            this.other = other;
            return this;
        }

        public Builder addEntry( final String term, final int count )
        {
            entries.add( new TermsFacetEntry( term, count ) );
            return this;
        }

        public TermsFacet build()
        {
            return new TermsFacet( this );
        }
    }
}
