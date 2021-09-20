package com.enonic.xp.util;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class BinaryReferences
    extends AbstractImmutableEntitySet<BinaryReference>
{
    private BinaryReferences( final ImmutableSet<BinaryReference> set )
    {
        super( set );
    }

    public static BinaryReferences empty()
    {
        return new BinaryReferences( ImmutableSet.of() );
    }

    public static BinaryReferences from( final String... binaryReferences )
    {
        return new BinaryReferences( Stream.of( binaryReferences ).map( BinaryReference::from ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static BinaryReferences from( final BinaryReference... binaryReferences )
    {
        return new BinaryReferences( ImmutableSet.copyOf( binaryReferences ) );
    }

    public static BinaryReferences from( final Iterable<BinaryReference> binaryReferences )
    {
        return new BinaryReferences( ImmutableSet.copyOf( binaryReferences ) );
    }
}
