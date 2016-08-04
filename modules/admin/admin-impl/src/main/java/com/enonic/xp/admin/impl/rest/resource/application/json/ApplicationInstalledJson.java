package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.rest.resource.application.ApplicationIconUrlResolver;
import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{
    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final boolean local, final ApplicationIconUrlResolver iconUrlResolver )
    {
        this.application = new ApplicationJson( application, local, null, null, null, iconUrlResolver );
    }

    @SuppressWarnings("unused")
    public ApplicationJson getApplication()
    {
        return application;
    }
}

