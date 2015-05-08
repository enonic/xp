package com.enonic.xp.content;

import com.google.common.annotations.Beta;

/**
 * Represents pushed content id with id of content that initially triggered push.
 * If content with id = <i>pushedContentId</i> was initially requested to push, then <i>reasonPushedId</i> = null;
 * Field <i>reasonPushedId</i> is not used in equals() and hashCode() methods to avoid duplicating content ids that were
 * published from few contents (for example, common child of two contents selected for publish).
 * Note, same content at the same time might be both child and parent for initially selected for publish contents,
 * in this case value of <i>reasonPushedId</i> field is not predicted.
 */
@Beta
public class PushedContentIdWithInitialReason
{
    private final ContentId pushedContentId;

    private final ContentId reasonPushedId;

    public PushedContentIdWithInitialReason( ContentId id )
    {
        this.pushedContentId = id;
        this.reasonPushedId = null;
    }

    public PushedContentIdWithInitialReason( ContentId pushed, ContentId reason )
    {
        this.pushedContentId = pushed;
        this.reasonPushedId = reason;
    }

    public ContentId getPushedContentId()
    {
        return pushedContentId;
    }

    public ContentId getInitialReasonPushedId()
    {
        return reasonPushedId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PushedContentIdWithInitialReason ) )
        {
            return false;
        }

        final PushedContentIdWithInitialReason that = (PushedContentIdWithInitialReason) o;

        if ( !pushedContentId.equals( that.pushedContentId ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return pushedContentId.hashCode();
    }
}
