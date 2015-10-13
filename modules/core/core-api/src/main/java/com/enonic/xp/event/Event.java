package com.enonic.xp.event;

import com.google.common.annotations.Beta;

@Beta
public interface Event
{
    default String getType()
    {
        return getClass().getSimpleName();
    }
}
