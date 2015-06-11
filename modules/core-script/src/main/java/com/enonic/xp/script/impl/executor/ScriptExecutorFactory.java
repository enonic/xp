package com.enonic.xp.script.impl.executor;

import com.enonic.xp.module.Module;

public interface ScriptExecutorFactory
{
    ScriptExecutor newExecutor( Module module );
}
