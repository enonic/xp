package com.enonic.wem.admin.json.module;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.module.ModuleFileEntry;

public class ModuleFileEntryJson
{
    private final ModuleFileEntry moduleFileEntry;

    private final ImmutableCollection<ModuleFileEntryJson> entries;

    public ModuleFileEntryJson( final ModuleFileEntry moduleFileEntry )
    {
        this.moduleFileEntry = moduleFileEntry;

        final ImmutableList.Builder<ModuleFileEntryJson> builder = ImmutableList.builder();
        for ( final ModuleFileEntry entry : moduleFileEntry.entries() )
        {
            builder.add( new ModuleFileEntryJson( entry ) );
        }
        this.entries = builder.build();
    }

    public String getName()
    {
        return moduleFileEntry.getName();
    }

    public String getResource()
    {
        return moduleFileEntry.getResource().toString();
    }

    public ImmutableCollection<ModuleFileEntryJson> getEntries()
    {
        return entries;
    }
}
