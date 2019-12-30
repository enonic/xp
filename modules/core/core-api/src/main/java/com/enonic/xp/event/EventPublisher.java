package com.enonic.xp.event;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface EventPublisher
{
    void publish( Event event );
}
