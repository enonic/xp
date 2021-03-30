package com.enonic.xp.script.graaljs.impl.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.function.ScriptFunctions;
import com.enonic.xp.script.graaljs.impl.service.ServiceRegistry;
import com.enonic.xp.script.graaljs.impl.value.ObjectScriptValue;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class ScriptEngineExecutorImpl
    implements ScriptExecutor
{
    private static final String PRE_SCRIPT = "(function(log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = " });";

    private final ScriptEngine engine;

    private final ScriptSettings scriptSettings;

    private final ScriptExportsCache exportsCache;

    private final ResourceService resourceService;

    private final Application application;

    private final ClassLoader classLoader;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Runnable> disposers = new ConcurrentHashMap<>();

    public ScriptEngineExecutorImpl( final ScriptSettings scriptSettings, final ResourceService resourceService,
                                     final Application application, final RunMode runMode )
    {
        this.engine = GraalJSScriptEngine.create( null, Context.newBuilder( "js" ) );

        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.application = application;
        this.classLoader = application.getClassLoader();
        this.exportsCache = new ScriptExportsCache( runMode, resourceService::getResource, this::runDisposers );

        final Bindings global = new SimpleBindings();
        global.putAll( this.scriptSettings.getGlobalVariables() );
        global.put( "app", ProxyObject.fromMap( buildAppInfo() ) );
        this.engine.setBindings( global, ScriptContext.GLOBAL_SCOPE );
    }

    @Override
    public Application getApplication()
    {
        return application;
    }

    @Override
    public ScriptExports executeMain( final ResourceKey key )
    {
        exportsCache.expireCacheIfNeeded();
        final ProxyObject exports = executeRequire( key );
        ScriptValue scriptValue = new ObjectScriptValue( exports );
        return new ScriptExportsImpl( key, scriptValue, exports );
    }

    @Override
    public CompletableFuture<ScriptExports> executeMainAsync( final ResourceKey key )
    {
        return null;
    }

    @Override
    public ProxyObject executeRequire( final ResourceKey key )
    {
        try
        {
            return exportsCache.getOrCompute( key, this::requireJsOrJson );
        }
        catch ( InterruptedException | TimeoutException e )
        {
            throw new RuntimeException( "Script require failed: [" + key + "]", e );
        }
    }

    @Override
    public ScriptValue newScriptValue( final Object value )
    {
        return null;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    @Override
    public ServiceRegistry getServiceRegistry()
    {
        return null;
    }

    @Override
    public ResourceService getResourceService()
    {
        return resourceService;
    }

    @Override
    public ScriptSettings getScriptSettings()
    {
        return scriptSettings;
    }

    @Override
    public void registerMock( final String name, final Object value )
    {

    }

    @Override
    public void registerDisposer( final ResourceKey key, final Runnable callback )
    {

    }

    @Override
    public void runDisposers()
    {

    }

    private ProxyObject requireJsOrJson( final Resource resource )
    {
        return "json".equals( resource.getKey().getExtension() ) ? requireJson( resource ) : requireJs( resource );
    }

    private ProxyObject requireJs( final Resource resource )
    {
        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        final Function<Object[], Object> func = doExecute( bindings, resource );
        return executeRequire( resource.getKey(), func );
    }


    private ProxyObject requireJson( final Resource resource )
    {
        throw new UnsupportedOperationException();
    }

    private ProxyObject executeRequire( final ResourceKey key, final Function<Object[], Object> func )
    {
        try
        {
            final ProxyObject exports = ProxyObject.fromMap( new HashMap<>() );

            final Map<String, Object> moduleAsMap = new HashMap<>();
            moduleAsMap.put( "id", key.toString() );
            moduleAsMap.put( "exports", exports );

            final ProxyObject module = ProxyObject.fromMap( moduleAsMap );

            final ScriptFunctions functions = new ScriptFunctions( key, this );

            Map<String, Object> javaBridge = new HashMap<>();
            javaBridge.put( "newBean", new ProxyExecutable()
            {
                @Override
                public Object execute( final Value... arguments )
                {
                    try
                    {
                        return functions.newBean( arguments[0].asString() );
                    }
                    catch ( Exception e )
                    {
                        return null;
                    }
                }
            } );

//            func.apply( new Object[]{functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module} );
            func.apply( new Object[]{functions.getLog(), functions.getRequire(), functions.getResolve(), ProxyObject.fromMap( javaBridge ), exports, module} );
            return exports;
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private String getFileName( final Resource resource )
    {
        if ( this.scriptSettings.getDebug() != null )
        {
            return this.scriptSettings.getDebug().scriptName( resource );
        }

        return resource.getKey().toString();
    }

    @SuppressWarnings("unchecked")
    private Function<Object[], Object> doExecute( final Bindings bindings, final Resource script )
    {
        try
        {
            final String text = script.readString();
            final String source = PRE_SCRIPT + text + POST_SCRIPT;
            return (Function<Object[], Object>) this.engine.eval( source, bindings );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Object> buildAppInfo()
    {
        final ApplicationInfoBuilder builder = new ApplicationInfoBuilder();
        builder.application( this.application );
        return builder.build();
    }
}
