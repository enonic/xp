package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.site.SiteDescriptor;

public final class ListApplicationJson
{
    private final List<ApplicationJson> list;

    public ListApplicationJson()
    {
        this.list = Lists.newArrayList();
    }

    public void add( final Application application, final SiteDescriptor siteDescriptor )
    {
        this.list.add( new ApplicationJson( application, siteDescriptor ) );
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
