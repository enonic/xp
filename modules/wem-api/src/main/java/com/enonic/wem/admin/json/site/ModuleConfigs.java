package com.enonic.wem.admin.json.site;

import java.util.List;

import com.enonic.wem.api.content.site.ModuleConfig;

public class ModuleConfigs
{
    private final List<ModuleConfig> moduleConfigList;

    public ModuleConfigs( final List<ModuleConfig> moduleConfigList )
    {
        this.moduleConfigList = moduleConfigList;
    }

    public List<ModuleConfig> toModuleConfigList()
    {
        return moduleConfigList;
    }
}
