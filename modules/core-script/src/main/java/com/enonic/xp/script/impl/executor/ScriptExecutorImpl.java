package com.enonic.xp.script.impl.executor;

import javax.script.ScriptEngine;

import com.enonic.xp.module.Module;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    protected Module module;

    protected ScriptEngine engine;

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        return null;
    }
}
