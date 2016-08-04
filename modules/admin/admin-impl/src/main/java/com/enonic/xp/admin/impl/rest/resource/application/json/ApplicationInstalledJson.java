package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{
    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final boolean local )
    {
        this.application = new ApplicationJson( application, local, null, null, null );
    }

    @SuppressWarnings("unused")
    public ApplicationJson getApplication()
    {
        return application;
    }
}

