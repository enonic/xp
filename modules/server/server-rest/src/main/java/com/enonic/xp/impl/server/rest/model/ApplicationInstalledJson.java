package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{
    private final ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final boolean local )
    {
        this.application = new ApplicationJson( application, local );
    }

    @SuppressWarnings("unused")
    public ApplicationJson getApplication()
    {
        return application;
    }
}
