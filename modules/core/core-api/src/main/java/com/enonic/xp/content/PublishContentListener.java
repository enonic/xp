package com.enonic.xp.content;

public interface PublishContentListener
{
    void contentPushed( int count );

    void contentResolved( int count );
}
