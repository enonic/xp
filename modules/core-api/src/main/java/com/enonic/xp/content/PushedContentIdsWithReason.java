package com.enonic.xp.content;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class PushedContentIdsWithReason
    extends AbstractImmutableEntitySet<PushedContentIdWithReason>
    implements Iterable<PushedContentIdWithReason>
{
    private PushedContentIdsWithReason( final ImmutableSet<PushedContentIdWithReason> set )
    {
        super( set );
    }

    public static PushedContentIdsWithReason empty()
    {
        final ImmutableSet<PushedContentIdWithReason> set = ImmutableSet.of();
        return new PushedContentIdsWithReason( set );
    }

    public static PushedContentIdsWithReason from( final PushedContentIdWithReason... ids )
    {
        return new PushedContentIdsWithReason( ImmutableSet.copyOf( ids ) );
    }

    public static PushedContentIdsWithReason from( final Iterable<PushedContentIdWithReason> ids )
    {
        return new PushedContentIdsWithReason( ImmutableSet.copyOf( ids ) );
    }

    public ContentIds getPushedContentIds()
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PushedContentIdWithReason pushed : getSet() )
        {
            ids.add( pushed.getPushedContentId() );
        }
        return ContentIds.from( ids );
    }
}
