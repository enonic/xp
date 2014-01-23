package com.enonic.wem.api.query.aggregation;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class Aggregations
    extends AbstractImmutableEntitySet<Aggregation>
{

    public static Aggregations empty()
    {
        final ImmutableSet<Aggregation> empty = ImmutableSet.of();
        return new Aggregations( empty );
    }

    public Aggregations( final ImmutableSet<Aggregation> set )
    {
        super( set );
    }

    public static class Builder
    {
        private Set<Aggregation> aggregations = Sets.newHashSet();


        public Builder add( final Aggregation aggregation )
        {
            this.aggregations.add( aggregation );
            return this;
        }

        public Aggregations build()
        {
            return new Aggregations( ImmutableSet.copyOf( aggregations ) );
        }
    }

}
