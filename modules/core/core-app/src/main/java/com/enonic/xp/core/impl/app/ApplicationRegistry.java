package com.enonic.xp.core.impl.app;

import java.util.Collection;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;

public interface ApplicationRegistry
{
    Application get( ApplicationKey key );

    Collection<Application> getAll();
}
