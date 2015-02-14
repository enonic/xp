package com.enonic.xp.admin.impl.rest.resource.module.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.module.ModuleJson;
import com.enonic.xp.core.module.Module;
import com.enonic.xp.core.module.Modules;

public class ListModuleJson
{

    private List<ModuleJson> list;

    public ListModuleJson( Modules modules )
    {
        ImmutableList.Builder<ModuleJson> builder = ImmutableList.builder();
        for ( Module module : modules )
        {
            builder.add( new ModuleJson( module ) );
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
