package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.site.SiteDescriptor;

public class ApplicationInstalledJson
{

    private ApplicationJson application;

    public ApplicationInstalledJson( final Application application, final SiteDescriptor siteDescriptor )
    {
        this.application = new ApplicationJson( application, siteDescriptor );
    }
}
