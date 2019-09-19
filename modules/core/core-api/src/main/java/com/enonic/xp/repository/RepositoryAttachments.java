package com.enonic.xp.repository;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class RepositoryAttachments
    extends AbstractImmutableEntitySet<RepositoryAttachment>
{
    private static final RepositoryAttachments EMPTY = new RepositoryAttachments( ImmutableSet.of() );

    private RepositoryAttachments( final ImmutableSet<RepositoryAttachment> set )
    {
        super( set );
    }

    public static RepositoryAttachments empty()
    {
        return EMPTY;
    }

    public static RepositoryAttachments from( final Iterable<RepositoryAttachment> repositoryAttachments )
    {
        return new RepositoryAttachments( ImmutableSet.copyOf( repositoryAttachments ) );
    }
}
