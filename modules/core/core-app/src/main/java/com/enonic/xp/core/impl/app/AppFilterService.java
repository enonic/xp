package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;

public interface AppFilterService
{
    boolean accept( ApplicationKey key );
}
