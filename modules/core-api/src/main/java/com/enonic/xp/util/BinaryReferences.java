package com.enonic.xp.util;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class BinaryReferences
    extends AbstractImmutableEntitySet<BinaryReference>
{
    private BinaryReferences( final ImmutableSet<BinaryReference> set )
    {
        super( set );
    }

    private BinaryReferences( final Set<BinaryReference> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static BinaryReferences empty()
    {
        final Set<BinaryReference> returnFields = Sets.newHashSet();
        return new BinaryReferences( returnFields );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<BinaryReference> binaryReferences = Sets.newHashSet();

        public Builder add( final BinaryReference binaryReference )
        {
            this.binaryReferences.add( binaryReference );
            return this;
        }

        public BinaryReferences build()
        {
            return new BinaryReferences( ImmutableSet.copyOf( this.binaryReferences ) );
        }
    }
}
