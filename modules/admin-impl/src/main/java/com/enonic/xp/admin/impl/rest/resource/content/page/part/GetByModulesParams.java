package com.enonic.xp.admin.impl.rest.resource.content.page.part;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

import com.enonic.wem.api.module.ModuleKeys;

public class GetByModulesParams
{

    private ModuleKeys moduleKeys;

    @JsonCreator
    public GetByModulesParams( @JsonProperty("moduleKeys") List<String> moduleKeysAsStringList )
    {
        this.moduleKeys = ModuleKeys.from( Iterables.toArray( moduleKeysAsStringList, String.class ) );
    }

    @JsonIgnore
    public ModuleKeys getModuleKeys()
    {
        return moduleKeys;
    }
}
