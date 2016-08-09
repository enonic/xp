package com.enonic.xp.content;

public interface PushContentListener
{
    enum PushResult
    {
        PUSHED, DELETED, FAILED
    }

    void contentPushed( ContentId contentId, PushResult result );

    void contentResolved( int count );
}
