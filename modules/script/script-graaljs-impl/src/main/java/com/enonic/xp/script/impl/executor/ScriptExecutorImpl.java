package com.enonic.xp.script.impl.executor;

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
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.JSContextFactory;
import com.enonic.xp.script.impl.function.ScriptFunctions;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class ScriptExecutorImpl
    implements ScriptExecutor
{
    private static final String PRE_SCRIPT = "(function( log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = " });";

    private final Executor asyncExecutor;

    private final Context context;

    private final ScriptSettings scriptSettings;

    private final ScriptExportsCache exportsCache;

    private final ClassLoader classLoader;

    private final ServiceRegistry serviceRegistry;

    private final ResourceService resourceService;

    private final Application application;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Runnable> disposers = new ConcurrentHashMap<>();

    private final ScriptValueFactory scriptValueFactory;

    private final JavascriptHelper javascriptHelper;

    public ScriptExecutorImpl( final Engine engine, final Executor asyncExecutor, final ScriptSettings scriptSettings,
                               final ServiceRegistry serviceRegistry, final ResourceService resourceService, final Application application,
                               final RunMode runMode )
    {
        this.asyncExecutor = asyncExecutor;
        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.serviceRegistry = serviceRegistry;
        this.application = application;

        this.context = JSContextFactory.create( engine, application.getClassLoader() );
        this.javascriptHelper = new JavascriptHelperFactory( context ).create();
        this.scriptValueFactory = new ScriptValueFactoryImpl( context, this.javascriptHelper );
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
        return scriptValueFactory.newValue( value );
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
    public JavascriptHelper getJavascriptHelper()
    {
        return javascriptHelper;
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

    @Override
    public void close()
    {
        if ( context != null )
        {
            context.close();
        }
    }

    private ScriptExports doExecuteMain( final ResourceKey key )
    {
        synchronized ( context )
        {
            final Object exports = executeRequire( key );
            ScriptValue scriptValue = scriptValueFactory.newValue( exports );
            return new ScriptExportsImpl( this.context, key, scriptValue, exports );
        }
    }

    private Value requireJsOrJson( final Resource resource )
    {
        return "json".equals( resource.getKey().getExtension() ) ? requireJson( resource ) : requireJs( resource );
    }

    private Value requireJs( final Resource resource )
    {
        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        synchronized ( context )
        {
            final Value func = doExecute( bindings, resource );
            return executeRequire( resource.getKey(), func );
        }
    }

    private Value requireJson( final Resource resource )
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

    private Value executeRequire( final ResourceKey script, final Value func )
    {
        try
        {
            Value exports = javascriptHelper.newJsObject();

            Value module = javascriptHelper.newJsObject();
            module.putMember( "id", script.toString() );
            module.putMember( "exports", exports );

            final ScriptFunctions functions = new ScriptFunctions( context, script, this );
            func.execute( functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module );
            return module.getMember( "exports" );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( script, "Script require failed: [" + script + "]", e );
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
            return this.context.eval( Source.newBuilder( "js", source, script.getKey().toString() ).build() );
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

    private Map<String, Object> buildAppInfo()
    {
        final ApplicationInfoBuilder builder = new ApplicationInfoBuilder();
        builder.application( this.application );
        return builder.build();
    }
}
