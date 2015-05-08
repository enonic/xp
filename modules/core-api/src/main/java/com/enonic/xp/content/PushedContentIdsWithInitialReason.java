package com.enonic.xp.content;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class PushedContentIdsWithInitialReason
    extends AbstractImmutableEntitySet<PushedContentIdWithInitialReason>
    implements Iterable<PushedContentIdWithInitialReason>
{
    private PushedContentIdsWithInitialReason( final ImmutableSet<PushedContentIdWithInitialReason> set )
    {
        super( set );
    }

    public static PushedContentIdsWithInitialReason empty()
    {
        final ImmutableSet<PushedContentIdWithInitialReason> set = ImmutableSet.of();
        return new PushedContentIdsWithInitialReason( set );
    }

    public static PushedContentIdsWithInitialReason from( final PushedContentIdWithInitialReason... ids )
    {
        return new PushedContentIdsWithInitialReason( ImmutableSet.copyOf( ids ) );
    }

    public static PushedContentIdsWithInitialReason from( final Iterable<PushedContentIdWithInitialReason> ids )
    {
        return new PushedContentIdsWithInitialReason( ImmutableSet.copyOf( ids ) );
    }

    public ContentIds getPushedContentIds()
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PushedContentIdWithInitialReason pushed : getSet() )
        {
            ids.add( pushed.getPushedContentId() );
        }
        return ContentIds.from( ids );
    }
}
