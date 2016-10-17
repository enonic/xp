package com.enonic.xp.content;

public interface PushContentListener
{
    void contentPushed( int count );

    void contentResolved( int count );
}
