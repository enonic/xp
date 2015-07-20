package com.enonic.xp.admin.impl.rest.resource.module.json;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.module.ModuleJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.Applications;
import com.enonic.xp.site.SiteDescriptor;

public class ListModuleJson
{

    private List<ModuleJson> list;

    public ListModuleJson( Applications applications, Iterable<SiteDescriptor> siteDescriptors )
    {
        ImmutableList.Builder<ModuleJson> builder = ImmutableList.builder();
        final Iterator<SiteDescriptor> siteDescriptorIterator = siteDescriptors.iterator();
        for ( Application application : applications )
        {
            builder.add( new ModuleJson( application, siteDescriptorIterator.next() ) );
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ModuleJson> getModules()
    {
        return this.list;
    }
}
