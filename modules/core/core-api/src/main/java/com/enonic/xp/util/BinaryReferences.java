package com.enonic.xp.util;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class BinaryReferences
    extends AbstractImmutableEntitySet<BinaryReference>
{
    private static final BinaryReferences EMPTY = new BinaryReferences( ImmutableSet.of() );

    private BinaryReferences( final ImmutableSet<BinaryReference> set )
    {
        super( set );
    }

    public static BinaryReferences empty()
    {
        return EMPTY;
    }

    public static BinaryReferences from( final String... binaryReferences )
    {
        return Arrays.stream( binaryReferences ).map( BinaryReference::from ).collect( collector() );
    }

    public static BinaryReferences from( final BinaryReference... binaryReferences )
    {
        return fromInternal( ImmutableSet.copyOf( binaryReferences ) );
    }

    public static BinaryReferences from( final Iterable<BinaryReference> binaryReferences )
    {
        return fromInternal( ImmutableSet.copyOf( binaryReferences ) );
    }

    public static Collector<BinaryReference, ?, BinaryReferences> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), BinaryReferences::fromInternal );
    }

    private static BinaryReferences fromInternal( final ImmutableSet<BinaryReference> set )
    {
        return set.isEmpty() ? EMPTY : new BinaryReferences( set );
    }
}
