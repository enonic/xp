package com.enonic.wem.api.aggregation;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class Buckets
    extends AbstractImmutableEntitySet<Bucket>
{

    public Buckets( final ImmutableSet<Bucket> buckets )
    {
        super( buckets );
    }

    public static class Builder
    {
        private Set<Bucket> buckets = Sets.newLinkedHashSet();

        public Builder addBucket( final Bucket bucket )
        {
            this.buckets.add( bucket );
            return this;
        }

        public Buckets build()
        {
            return new Buckets( ImmutableSet.copyOf( this.buckets ) );
        }
    }


}
