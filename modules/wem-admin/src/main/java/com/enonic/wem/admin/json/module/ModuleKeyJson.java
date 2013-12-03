package com.enonic.wem.admin.json.module;

import com.enonic.wem.api.module.ModuleKey;

public class ModuleKeyJson
{
    private final ModuleKey moduleKey;

    public ModuleKeyJson( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
    }

    public String getKey()
    {
        return moduleKey.toString();
    }

    public String getName()
    {
        return moduleKey.getName().toString();
    }

    public String getVersion()
    {
        return moduleKey.getVersion().toString();
    }

    public String getRefString()
    {
        return moduleKey.getRefString();
    }
}
