package com.enonic.wem.core.script.engine;

import javax.script.Bindings;

import com.enonic.wem.core.module.source.ModuleSource;

public interface ScriptEngineService
{
    public Bindings createBindings();

    public ExecutableScript compile( ModuleSource source );
}
