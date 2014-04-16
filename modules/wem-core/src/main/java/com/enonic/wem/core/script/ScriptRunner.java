package com.enonic.wem.core.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptRunner
{
    public ScriptRunner source( ResourceKey source );

    public ScriptRunner binding( String name, Object value );

    public void execute();
}
