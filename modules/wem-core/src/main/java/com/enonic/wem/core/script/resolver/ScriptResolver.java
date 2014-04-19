package com.enonic.wem.core.script.resolver;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptResolver
{
    public ModuleResourceKey getResource();

    public ModuleResourceKey resolveScript( String name );

    public ModuleResourceKey resolveResource( String name );
}
