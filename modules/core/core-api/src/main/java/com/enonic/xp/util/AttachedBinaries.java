package com.enonic.xp.util;

import java.util.NoSuchElementException;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class AttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private static final AttachedBinaries EMPTY = new AttachedBinaries( ImmutableSet.of() );

    private AttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
    }

    public AttachedBinary getByBinaryReference( BinaryReference binaryReference )
    {
        return set.stream().filter( att -> Objects.equals( binaryReference, att.getBinaryReference() ) ).findAny().
            orElseThrow( NoSuchElementException::new );
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
