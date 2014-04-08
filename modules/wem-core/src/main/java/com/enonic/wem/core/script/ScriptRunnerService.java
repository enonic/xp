package com.enonic.wem.core.script;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptRunnerService
{
    public ScriptRunner newRunner( ModuleResourceKey resource );
}
