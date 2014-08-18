package com.enonic.wem.portal.script.runner;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptRunner
{
    public ScriptRunner source( ResourceKey source );

    public ScriptRunner property( String name, Object value );

    public void execute();
}
