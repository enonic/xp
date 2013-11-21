package com.enonic.wem.api.content.site;

import java.util.List;

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
