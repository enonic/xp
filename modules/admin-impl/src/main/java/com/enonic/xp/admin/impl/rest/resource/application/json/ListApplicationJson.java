package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.Applications;
import com.enonic.xp.site.SiteDescriptor;

public class ListApplicationJson
{

    private List<ApplicationJson> list;

    public ListApplicationJson( Applications applications, Iterable<SiteDescriptor> siteDescriptors )
    {
        ImmutableList.Builder<ApplicationJson> builder = ImmutableList.builder();
        final Iterator<SiteDescriptor> siteDescriptorIterator = siteDescriptors.iterator();
        for ( Application application : applications )
        {
            builder.add( new ApplicationJson( application, siteDescriptorIterator.next() ) );
        }
        this.list = builder.build();
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
