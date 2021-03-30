package com.enonic.xp.script.graaljs.impl.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.io.Files;

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

public class ScriptExecutorImpl
    implements ScriptExecutor
{
    private static final String PRE_SCRIPT = "(function( log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = " });";

    private final Context context;

    private final Executor asyncExecutor;

    private final ScriptSettings scriptSettings;

    private final ServiceRegistry serviceRegistry;

    private final ScriptExportsCache exportsCache;

    private final ResourceService resourceService;

    private final Application application;

    private final ClassLoader classLoader;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Runnable> disposers = new ConcurrentHashMap<>();

    public ScriptExecutorImpl( final Context context, final Executor asyncExecutor, final ScriptSettings scriptSettings,
                               final ServiceRegistry serviceRegistry, final ResourceService resourceService, final Application application,
                               final RunMode runMode )
    {
        this.context = context;
        this.asyncExecutor = asyncExecutor;
        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.serviceRegistry = serviceRegistry;
        this.application = application;
        this.classLoader = application.getClassLoader();
        this.exportsCache = new ScriptExportsCache( runMode, resourceService::getResource, this::runDisposers );

        final Map<String, Object> globalVariables = new HashMap<>( this.scriptSettings.getGlobalVariables() );
        globalVariables.put( "app", ProxyObject.fromMap( buildAppInfo() ) );
        globalVariables.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
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
        return doExecuteMain( key );
    }

    @Override
    public CompletableFuture<ScriptExports> executeMainAsync( final ResourceKey key )
    {
        exportsCache.expireCacheIfNeeded();
        return CompletableFuture.completedFuture( key ).thenApplyAsync( this::doExecuteMain, asyncExecutor );
    }

    @Override
    public Object executeRequire( final ResourceKey key )
    {
        final Object mock = this.mocks.get( key.getPath() );
        if ( mock != null )
        {
            return mock;
        }

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
        return serviceRegistry;
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
        this.mocks.put( name, value );

        if ( Files.getFileExtension( name ).isEmpty() )
        {
            this.mocks.put( name + ".js", value );
        }
    }

    @Override
    public void registerDisposer( final ResourceKey key, final Runnable callback )
    {
        this.disposers.put( key, callback );
    }

    @Override
    public void runDisposers()
    {
        this.disposers.values().forEach( Runnable::run );
    }

    private ScriptExports doExecuteMain( final ResourceKey key )
    {
        final Object exports = executeRequire( key );
        ScriptValue scriptValue = new ObjectScriptValue( exports );
        return new ScriptExportsImpl( key, scriptValue, exports );
    }

    private ProxyObject requireJsOrJson( final Resource resource )
    {
        return "json".equals( resource.getKey().getExtension() ) ? requireJson( resource ) : requireJs( resource );
    }

    private ProxyObject requireJs( final Resource resource )
    {
        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        final Value func = doExecute( bindings, resource );
        return executeRequire( resource.getKey(), func );
    }

    private ProxyObject requireJson( final Resource resource )
    {
        throw new UnsupportedOperationException();
    }

    private ProxyObject executeRequire( final ResourceKey script, final Value func )
    {
        try
        {
            final ProxyObject exports = ProxyObject.fromMap( new HashMap<>() );

            final Map<String, Object> moduleAsMap = new HashMap<>();
            moduleAsMap.put( "id", script.toString() );
            moduleAsMap.put( "exports", exports );

            final ProxyObject module = ProxyObject.fromMap( moduleAsMap );

            final ScriptFunctions functions = new ScriptFunctions( script, this );
            func.execute( functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module );
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

    private Value doExecute( final Bindings bindings, final Resource script )
    {
        try
        {
            final String text = script.readString();
            final String source = PRE_SCRIPT + text + POST_SCRIPT;
            bindings.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
            return this.context.eval( "js", source );
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
