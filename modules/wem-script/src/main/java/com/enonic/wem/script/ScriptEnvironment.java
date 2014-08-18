package com.enonic.wem.script;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptEnvironment
{
    public ResourceKey getLibrary( String name );

    public Map<String, Object> getGlobalVariables();
}
