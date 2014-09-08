package com.enonic.wem.script.internal.v2;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.Bindings;
import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;
import com.enonic.wem.script.internal.ScriptEnvironment;

@Singleton
public final class ScriptServiceImpl
    implements ScriptService
{
    private final ScriptExecutor executor;

    @Inject
    public ScriptServiceImpl( final ScriptEnvironment environment )
    {
        final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        this.executor = new ScriptExecutorImpl( engine, environment );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptModuleScope scope = new ScriptModuleScope( script, this.executor );
        final Bindings bindings = scope.executeThis();
        return new ScriptExportsImpl( script, this.executor, bindings );
    }
}
