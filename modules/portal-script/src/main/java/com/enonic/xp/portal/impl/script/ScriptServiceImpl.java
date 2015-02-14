package com.enonic.xp.portal.impl.script;

import java.util.Map;

import javax.script.ScriptEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.portal.script.ScriptService;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.impl.script.invoker.CommandInvokerImpl;

@Component(immediate = true)
public final class ScriptServiceImpl
    implements ScriptService
{
    private final ScriptEngine engine;

    private final CommandInvokerImpl invoker;

    private final Map<String, Object> globalMap;

    public ScriptServiceImpl()
    {
        this.engine = new NashornScriptEngineFactory().getScriptEngine();
        this.invoker = new CommandInvokerImpl();
        this.globalMap = Maps.newHashMap();
    }

    public void addGlobalVariable( final String key, final Object value )
    {
        this.globalMap.put( key, value );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setEngine( this.engine );
        executor.setInvoker( this.invoker );
        executor.setGlobalMap( this.globalMap );
        executor.setScript( script );

        final Object exports = executor.executeMain();
        final ScriptValue value = executor.newScriptValue( exports );
        return new ScriptExportsImpl( script, value );
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
