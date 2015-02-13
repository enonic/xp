package com.enonic.wem.script.internal.bean;

import com.enonic.wem.api.module.ModuleKey;

public final class ModuleScriptInfo
{
    private final ModuleKey key;

    public ModuleScriptInfo( final ModuleKey key )
    {
        this.key = key;
    }

    public String getName()
    {
        return this.key.toString();
    }
}
