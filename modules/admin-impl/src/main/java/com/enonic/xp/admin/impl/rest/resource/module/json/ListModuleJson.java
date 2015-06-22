package com.enonic.xp.admin.impl.rest.resource.module.json;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.module.ModuleJson;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.Modules;
import com.enonic.xp.site.SiteDescriptor;

public class ListModuleJson
{

    private List<ModuleJson> list;

    public ListModuleJson( Modules modules, Iterable<SiteDescriptor> siteDescriptors )
    {
        ImmutableList.Builder<ModuleJson> builder = ImmutableList.builder();
        final Iterator<SiteDescriptor> siteDescriptorIterator = siteDescriptors.iterator();
        for ( Module module : modules )
        {
            builder.add( new ModuleJson( module, siteDescriptorIterator.next() ) );
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
