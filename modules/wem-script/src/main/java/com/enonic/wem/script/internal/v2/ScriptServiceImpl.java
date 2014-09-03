package com.enonic.wem.script.internal.v2;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.v2.ScriptExports;
import com.enonic.wem.script.v2.ScriptService;

public final class ScriptServiceImpl
    implements ScriptService
{
    private final ScriptExecutor executor;

    public ScriptServiceImpl()
    {
        final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        this.executor = new ScriptExecutorImpl( engine );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptModuleScope scope = new ScriptModuleScope( script, this.executor );
        final Bindings bindings = scope.executeThis();
        return new ScriptExportsImpl( script, this.executor, bindings );
    }
}
