package com.enonic.xp.util;

import java.util.Set;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

import static java.util.stream.Collectors.toSet;

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

    public static BinaryReferences from( final String... binaryReferences )
    {
        final Set<BinaryReference> binaryReferenceList = Stream.of( binaryReferences ).map( BinaryReference::from ).collect( toSet() );
        return new BinaryReferences( ImmutableSet.copyOf( binaryReferenceList ) );
    }

    public static BinaryReferences from( final BinaryReference... binaryReferences )
    {
        return new BinaryReferences( ImmutableSet.copyOf( binaryReferences ) );
    }

    public static BinaryReferences from( final Iterable<BinaryReference> binaryReferences )
    {
        final ImmutableSet.Builder<BinaryReference> keys = ImmutableSet.builder();
        for ( BinaryReference binaryReference : binaryReferences )
        {
            keys.add( binaryReference );
        }
        return new BinaryReferences( keys.build() );
    }

}
