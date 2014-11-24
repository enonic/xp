package com.enonic.wem.script.internal;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.internal.invoker.CommandInvokerImpl;

@Component(immediate = true)
public final class ScriptServiceImpl
    implements ScriptService
{
    private final CommandInvokerImpl invoker;

    private final ScriptExecutor executor;

    public ScriptServiceImpl()
    {
        this.invoker = new CommandInvokerImpl();
        final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        this.executor = new ScriptExecutorImpl( engine, this.invoker );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final Bindings exports = this.executor.executeRequire( script );
        return new ScriptExportsImpl( script, this.executor, exports );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final CommandHandler handler )
    {
        this.invoker.register( handler );
    }

    public void removeHandler( final CommandHandler handler )
    {
        this.invoker.unregister( handler );
    }
}
