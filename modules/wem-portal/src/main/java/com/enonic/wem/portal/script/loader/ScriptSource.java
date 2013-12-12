package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptSource
{
    public String getName();

    public String getLocation();

    public String getCacheKey();

    public String getScriptAsString();

    public ModuleResourceKey getResourceKey();

    public long getTimestamp();
}
