package com.enonic.xp.data;

import com.google.common.annotations.Beta;

@Beta
public interface PropertyIdProvider
{
    PropertyId nextId();
}
