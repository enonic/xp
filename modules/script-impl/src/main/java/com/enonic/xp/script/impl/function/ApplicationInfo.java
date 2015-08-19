package com.enonic.xp.script.impl.function;

import com.enonic.xp.app.Application;

public final class ApplicationInfo
{
    private final Application application;

    public ApplicationInfo( final Application application )
    {
        this.application = application;
    }

    public String getName()
    {
        return this.application.getKey().getName();
    }

    public String getVersion()
    {
        return this.application.getVersion().toString();
    }
}
