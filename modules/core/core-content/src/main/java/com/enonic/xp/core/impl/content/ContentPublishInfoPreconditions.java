package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPublishInfo;

class ContentPublishInfoPreconditions
{
    private ContentPublishInfoPreconditions()
    {
    }

    static void check( final ContentPublishInfo publishInfo )
    {
        if ( publishInfo != null )
        {
            final Instant publishToInstant = publishInfo.getTo();
            if ( publishToInstant != null )
            {
                final Instant publishFromInstant = publishInfo.getFrom();
                Preconditions.checkArgument( publishFromInstant != null, "'Publish from' must be set if 'Publish to' is set" );
                Preconditions.checkArgument( publishToInstant.isAfter( publishFromInstant ),
                                             "'Publish to' must be set after 'Publish from'" );
            }
        }
    }
}
