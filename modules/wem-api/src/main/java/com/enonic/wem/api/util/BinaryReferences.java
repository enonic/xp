package com.enonic.wem.api.util;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class BinaryReferences
    extends AbstractImmutableEntitySet<BinaryReference>
{
    public BinaryReferences( final ImmutableSet<BinaryReference> set )
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

    private static BinaryReferences fromCollection( final Collection<BinaryReference> references )
    {
        return new BinaryReferences( ImmutableSet.copyOf( references ) );
    }

}
