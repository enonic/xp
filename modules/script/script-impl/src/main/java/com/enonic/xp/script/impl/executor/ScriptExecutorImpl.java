package com.enonic.xp.script.impl.executor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.io.Files;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.function.ScriptFunctions;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.util.NashornHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private static final String PRE_SCRIPT = "(function(log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = "\n});";

    private final Executor asyncExecutor;

    private final ScriptEngine engine;

    private final ScriptSettings scriptSettings;

    private final ScriptExportsCache<Object> exportsCache;

    private final ClassLoader classLoader;

    private final ServiceRegistry serviceRegistry;

    private final ResourceService resourceService;

    private final Application application;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Runnable> disposers = new ConcurrentHashMap<>();

    private final ScriptValueFactory<Bindings> scriptValueFactory;

    private final JavascriptHelper<Bindings> javascriptHelper;

    public ScriptExecutorImpl( final Executor asyncExecutor, final ScriptSettings scriptSettings, final ClassLoader classLoader,
                               final ServiceRegistry serviceRegistry, final ResourceService resourceService, final Application application,
                               final RunMode runMode )
    {
        this.asyncExecutor = asyncExecutor;
        this.engine = NashornHelper.getScriptEngine( classLoader );
        this.scriptSettings = scriptSettings;
        this.classLoader = classLoader;
        this.serviceRegistry = serviceRegistry;
        this.resourceService = resourceService;
        this.application = application;
        this.javascriptHelper = new JavascriptHelperFactory( this.engine ).create();
        this.scriptValueFactory = new ScriptValueFactoryImpl( this.javascriptHelper );
        this.exportsCache = new ScriptExportsCache<>( runMode, resourceService::getResource, this::runDisposers );

        final Bindings global = new SimpleBindings();
        global.putAll( this.scriptSettings.getGlobalVariables() );
        global.put( "app", buildAppInfo() );
        this.engine.setBindings( global, ScriptContext.GLOBAL_SCOPE );
    }

    private Map<String, Object> buildAppInfo()
    {
        final ApplicationInfoBuilder builder = new ApplicationInfoBuilder();
        builder.application( this.application );
        builder.mapSupplier( this.javascriptHelper::newJsObject );
        return builder.build();
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

    private ScriptExports doExecuteMain( final ResourceKey key )
    {
        final Object exports = executeRequire( key );
        final ScriptValue value = newScriptValue( exports );
        return new ScriptExportsImpl( key, value, exports );
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

    private String getFileName( final Resource resource )
    {
        if ( this.scriptSettings.getDebug() != null )
        {
            return this.scriptSettings.getDebug().scriptName( resource );
        }

        return resource.getKey().toString();
    }

    private Object executeRequire( final ResourceKey key, final JSObject func )
    {
        try
        {
            final Bindings exports = this.javascriptHelper.newJsObject();

            final Bindings module = this.javascriptHelper.newJsObject();
            module.put( "id", key.toString() );
            module.put( "exports", exports );

            final ScriptFunctions functions = new ScriptFunctions( key, this );
            func.call( exports, functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module );
            return module.get( "exports" );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( key, "Script require failed: [" + key + "]", e );
        }
    }

    @Override
    public ScriptValue newScriptValue( final Object value )
    {
        return this.scriptValueFactory.newValue( value );
    }

    private JSObject doExecute( final Bindings bindings, final Resource script )
    {
        try
        {
            final String text = script.readString();
            final String source = PRE_SCRIPT + text + POST_SCRIPT;
            return (JSObject) this.engine.eval( source, bindings );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( script.getKey(), "Script execute failed: [" + script.getKey() + "]", e );
        }
    }

    private Object requireJsOrJson( final Resource resource )
    {
        return "json".equals( resource.getKey().getExtension() ) ? requireJson( resource ) : requireJs( resource );
    }

    private Object requireJs( final Resource resource )
    {
        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        final JSObject func = doExecute( bindings, resource );
        return executeRequire( resource.getKey(), func );
    }

    private Object requireJson( final Resource resource )
    {
        try
        {
            final String text = resource.readString();
            return this.javascriptHelper.parseJson( text );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
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
    public Application getApplication()
    {
        return this.application;
    }

    @Override
    public ScriptSettings getScriptSettings()
    {
        return this.scriptSettings;
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
    public ObjectConverter getObjectConverter()
    {
        return this.javascriptHelper.objectConverter();
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
}
