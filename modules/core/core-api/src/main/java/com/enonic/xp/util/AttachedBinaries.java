package com.enonic.xp.util;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class AttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private static final AttachedBinaries EMPTY = new AttachedBinaries( ImmutableSet.of() );

    private AttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
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
