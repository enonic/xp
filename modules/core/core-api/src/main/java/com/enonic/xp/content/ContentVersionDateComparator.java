package com.enonic.xp.content;

import java.time.Instant;
import java.util.Comparator;

final class ContentVersionDateComparator
    implements Comparator<ContentVersion>
{
    public static final ContentVersionDateComparator INSTANCE = new ContentVersionDateComparator();

    private ContentVersionDateComparator()
    {
    }

    @Override
    public int compare( final ContentVersion thisVersion, final ContentVersion thatVersion )
    {
        Instant thisTime;
        Instant thatTime;

        if ( thisVersion.getPublishInfo() != null && thisVersion.getPublishInfo().getTimestamp() != null )
        {
            thisTime = thisVersion.getPublishInfo().getTimestamp();
        }
        else
        {
            thisTime = thisVersion.getModified();

        }
        if ( thatVersion.getPublishInfo() != null && thatVersion.getPublishInfo().getTimestamp() != null )
        {
            thatTime = thatVersion.getPublishInfo().getTimestamp();
        }
        else
        {
            thatTime = thatVersion.getModified();
        }

        return thatTime.compareTo( thisTime );
    }
}
