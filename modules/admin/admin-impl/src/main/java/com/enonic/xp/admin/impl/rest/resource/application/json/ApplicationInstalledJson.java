package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.rest.resource.application.ApplicationIconUrlResolver;
import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{
    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final boolean local, final ApplicationIconUrlResolver iconUrlResolver )
    {
        this.application = ApplicationJson.create().
            setApplication( application ).
            setLocal( local ).
            setIconUrlResolver( iconUrlResolver ).
            build();
    }

    @SuppressWarnings("unused")
    public ApplicationJson getApplication()
    {
        return application;
    }
}

