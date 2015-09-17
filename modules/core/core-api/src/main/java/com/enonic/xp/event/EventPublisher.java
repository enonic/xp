package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
public interface EventPublisher
{

    void publish( Event event );

}
