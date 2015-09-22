package com.enonic.xp.script.runtime;

public interface ScriptRuntimeFactory
{
    ScriptRuntime create( ScriptSettings settings );

    void dispose( ScriptRuntime runtime );
}
