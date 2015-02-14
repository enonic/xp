package com.enonic.xp.admin.impl.rest.resource.module.json;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.module.ModuleKey;

public final class ModuleListParams
{
    private final Set<ModuleKey> moduleKeys;

    @JsonCreator
    public ModuleListParams( @JsonProperty("key") String... keys )
    {
        this.moduleKeys = Sets.newHashSet();
        for ( final String key : keys )
        {
            this.moduleKeys.add( ModuleKey.from( key ) );
        }
    }

    public Set<ModuleKey> getModuleKeys()
    {
        return this.moduleKeys;
    }
}
