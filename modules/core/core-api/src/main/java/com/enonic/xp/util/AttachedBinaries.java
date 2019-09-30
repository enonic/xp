package com.enonic.xp.util;

import java.util.function.Function;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class AttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private static final AttachedBinaries EMPTY = new AttachedBinaries( ImmutableSet.of() );

    private final ImmutableMap<BinaryReference, AttachedBinary> binaryReferenceMap;

    private AttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
        binaryReferenceMap = set.stream().collect( ImmutableMap.toImmutableMap( AttachedBinary::getBinaryReference, Function.identity() ) );
    }

    public AttachedBinary getByBinaryReference( BinaryReference binaryReference )
    {
        return binaryReferenceMap.get( binaryReference );
    }

    public static AttachedBinaries empty()
    {
        return EMPTY;
    }

    public static AttachedBinaries from( final Iterable<AttachedBinary> repositoryAttachments )
    {
        return new AttachedBinaries( ImmutableSet.copyOf( repositoryAttachments ) );
    }
}
