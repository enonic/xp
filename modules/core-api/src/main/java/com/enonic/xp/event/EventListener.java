package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
@FunctionalInterface
public interface EventListener
{

    void onEvent(Event event);

}
