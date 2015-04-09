package com.enonic.xp.aggregation;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
        private final Set<Bucket> buckets = Sets.newLinkedHashSet();

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
