package com.enonic.xp.admin.impl.json.content.site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.module.ModuleKey;

public class ModuleConfigJson
{
    private final ModuleConfig moduleConfig;

    private final List<PropertyArrayJson> configAsJson;

    @JsonCreator
    ModuleConfigJson( @JsonProperty("moduleKey") final String moduleKey,
                      @JsonProperty("config") final List<PropertyArrayJson> configAsDataJsonList )
    {
        configAsJson = configAsDataJsonList;

        this.moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( moduleKey ) ).
            config( configAsJson != null ? PropertyTreeJson.fromJson( configAsJson ) : null ).
            build();
    }

    public ModuleConfigJson( final ModuleConfig moduleConfig )
    {
        this.moduleConfig = moduleConfig;
        this.configAsJson = PropertyTreeJson.toJson( moduleConfig.getConfig() );
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

    public String getModuleKey()
    {
        return moduleConfig.getModule().toString();
    }

    public List<PropertyArrayJson> getConfig()
    {
        return configAsJson;
    }

    @JsonIgnore
    public ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }


}
