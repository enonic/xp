package com.enonic.xp.aggregation;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class Buckets
    extends AbstractImmutableEntitySet<Bucket>
{
    private Buckets( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.buckets ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<Bucket> buckets = new LinkedHashSet<>();

        public Builder add( final Bucket bucket )
        {
            this.buckets.add( bucket );
            return this;
        }

        public Buckets build()
        {
            return new Buckets( this );
        }
    }


}
