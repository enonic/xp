package com.enonic.xp.repository;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.AttachedBinary;

@Beta
public final class RepositoryAttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private static final RepositoryAttachedBinaries EMPTY = new RepositoryAttachedBinaries( ImmutableSet.of() );

    private RepositoryAttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
    }

    public static RepositoryAttachedBinaries empty()
    {
        return EMPTY;
    }

    public static RepositoryAttachedBinaries from( final Iterable<AttachedBinary> repositoryAttachments )
    {
        return new RepositoryAttachedBinaries( ImmutableSet.copyOf( repositoryAttachments ) );
    }
}
