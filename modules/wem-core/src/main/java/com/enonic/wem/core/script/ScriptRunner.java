package com.enonic.wem.core.script;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptRunner
{
    public ScriptRunner source( ModuleResourceKey source );

    public ScriptRunner binding( String name, Object value );

    public void execute();
}
