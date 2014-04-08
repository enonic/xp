package com.enonic.wem.core.script.engine;

import javax.script.Bindings;
import javax.script.CompiledScript;

import com.enonic.wem.core.module.source.ModuleSource;

public interface ExecutableScript
{
    public ModuleSource getSource();

    public CompiledScript getScript();

    public Object execute( Bindings bindings );
}
