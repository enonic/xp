package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.application.ApplicationIconUrlResolver;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.site.SiteDescriptor;

public final class ListApplicationJson
{
    private final List<ApplicationJson> list;

    public ListApplicationJson()
    {
        this.list = Lists.newArrayList();
    }

    public void add( final Application application, final boolean local, final ApplicationDescriptor applicationDescriptor,
                     final SiteDescriptor siteDescriptor, final AuthDescriptor authDescriptor,
                     final ApplicationIconUrlResolver iconUrlResolver )
    {
        this.list.add( new ApplicationJson( application, local, applicationDescriptor, siteDescriptor, authDescriptor, iconUrlResolver ) );
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ApplicationJson> getApplications()
    {
        return this.list;
    }
}
