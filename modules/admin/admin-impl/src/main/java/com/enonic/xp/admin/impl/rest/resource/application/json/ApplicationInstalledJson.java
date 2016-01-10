package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.app.Application;

public class ApplicationInstalledJson
{

    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application )
    {
        this.application = new ApplicationJson( application, null );
    }
}
