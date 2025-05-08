package com.enonic.xp.query.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class BooleanFilter
    extends Filter
{
    final ImmutableList<Filter> must;

    final ImmutableList<Filter> mustNot;

    final ImmutableList<Filter> should;

    public BooleanFilter( final Builder builder )
    {
        super( builder );
        this.must = ImmutableList.copyOf( builder.must );
        this.mustNot = ImmutableList.copyOf( builder.mustNot );
        this.should = ImmutableList.copyOf( builder.should );
    }

    public List<Filter> getMust()
    {
        return must;
    }

    public List<Filter> getMustNot()
    {
        return mustNot;
    }

    public List<Filter> getShould()
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
        List<Filter> must = new ArrayList<>();

        List<Filter> mustNot = new ArrayList<>();

        List<Filter> should = new ArrayList<>();

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
