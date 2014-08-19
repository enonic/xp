package com.enonic.wem.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptRunner
{
    public ScriptRunner source( ResourceKey source );

    public ScriptRunner property( String name, Object value );

    public void execute();
}
