package com.enonic.wem.api.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.util.AbstractImmutableEntitySet;

public final class ContentIds
    extends AbstractImmutableEntitySet<ContentId>
    implements ContentSelectors<ContentId>
{
    private ContentIds( final ImmutableSet<ContentId> set )
    {
        super( set );
    }

    public static ContentIds empty()
    {
        final ImmutableSet<ContentId> set = ImmutableSet.of();
        return new ContentIds( set );
    }

    public static ContentIds from( final ContentId... ids )
    {
        return new ContentIds( ImmutableSet.copyOf( ids ) );
    }

    public static ContentIds from( final Iterable<ContentId> ids )
    {
        return new ContentIds( ImmutableSet.copyOf( ids ) );
    }
}
