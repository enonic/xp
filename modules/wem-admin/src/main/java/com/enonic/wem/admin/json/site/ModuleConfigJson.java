package com.enonic.wem.admin.json.site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.module.ModuleKey;

public class ModuleConfigJson
{
    private final ModuleConfig moduleConfig;

    private final RootDataSetJson configAsJson;

    @JsonCreator
    ModuleConfigJson( @JsonProperty("module") final String module, @JsonProperty("config") final List<DataJson> configAsDataJsonList )
    {
        configAsJson = new RootDataSetJson( configAsDataJsonList );

        this.moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( module ) ).
            config( configAsJson.getRootDataSet() ).
            build();
    }

    public ModuleConfigJson( final ModuleConfig moduleConfig )
    {
        this.moduleConfig = moduleConfig;
        this.configAsJson = new RootDataSetJson( moduleConfig.getConfig() );
    }

    public String getModule()
    {
        return moduleConfig.getModule().toString();
    }

    public List<DataJson> getConfig()
    {
        return configAsJson.getValue();
    }

    @JsonIgnore
    public ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }

    public static ModuleConfigs toModuleConfigs( final Collection<ModuleConfigJson> moduleConfigs )
    {
        final List<ModuleConfig> list = new ArrayList<>();

        for ( final ModuleConfigJson moduleConfigJson : moduleConfigs )
        {
            list.add( moduleConfigJson.getModuleConfig() );
        }

        return ModuleConfigs.from( list );
    }


}
