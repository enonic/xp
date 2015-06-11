package com.enonic.xp.script.impl.executor;

import com.enonic.xp.module.Module;
import com.enonic.xp.script.impl.util.NashornHelper;

public final class ScriptExecutorFactoryImpl
    implements ScriptExecutorFactory
{
    @Override
    public ScriptExecutor newExecutor( final Module module )
    {
        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.module = module;
        executor.engine = NashornHelper.getScriptEngine( module.getClassLoader(), "-strict" );
        return executor;
    }
}
