package com.enonic.xp.query.filter;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class BooleanFilter
    extends Filter
{
    final ImmutableSet<Filter> must;

    final ImmutableSet<Filter> mustNot;

    final ImmutableSet<Filter> should;

    public BooleanFilter( final Builder builder )
    {
        super( builder );
        this.must = ImmutableSet.copyOf( builder.must );
        this.mustNot = ImmutableSet.copyOf( builder.mustNot );
        this.should = ImmutableSet.copyOf( builder.should );
    }

    public ImmutableSet<Filter> getMust()
    {
        return must;
    }

    public ImmutableSet<Filter> getMustNot()
    {
        return mustNot;
    }

    public ImmutableSet<Filter> getShould()
    {
        return should;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "must", must ).
            add( "mustNot", mustNot ).
            add( "should", should ).
            toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Filter.Builder<Builder>
    {
        Set<Filter> must = new HashSet<>();

        Set<Filter> mustNot = new HashSet<>();

        Set<Filter> should = new HashSet<>();

        public Builder must( final Filter filter )
        {
            this.must.add( filter );
            return this;
        }

        public Builder mustNot( final Filter filter )
        {
            this.mustNot.add( filter );
            return this;
        }

        public Builder should( final Filter filter )
        {
            this.should.add( filter );
            return this;
        }

        public BooleanFilter build()
        {
            return new BooleanFilter( this );

        }

    }


}
