package com.enonic.xp.script.graal.executor;

import java.io.Closeable;
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
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.io.Files;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.function.GraalScriptFunctions;
import com.enonic.xp.script.graal.util.GraalErrorHelper;
import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;
import com.enonic.xp.script.graal.value.GraalScriptValueFactory;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExportsCache;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class GraalScriptExecutor
    implements ScriptExecutor, Closeable
{
    private static final String PRE_SCRIPT = "(function( log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = "\n});";

    private final Executor asyncExecutor;

    private final Context context;

    private final ScriptSettings scriptSettings;

    private final ScriptExportsCache<Value> exportsCache;

    private final ClassLoader classLoader;

    private final ServiceRegistry serviceRegistry;

    private final ResourceService resourceService;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Runnable> disposers = new ConcurrentHashMap<>();

    private final ScriptValueFactory<Value> scriptValueFactory;

    private final JavascriptHelper<Value> javascriptHelper;

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final Executor asyncExecutor, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application )
    {
        this.asyncExecutor = asyncExecutor;
        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.serviceRegistry = serviceRegistry;
        this.classLoader = classLoader;

        final GraalScriptValueFactory scriptValueFactory =
            new GraalScriptValueFactory( contextFactory, new GraalJavascriptHelperFactory() );
        this.scriptValueFactory = scriptValueFactory;
        this.javascriptHelper = this.scriptValueFactory.getJavascriptHelper();
        this.exportsCache = new ScriptExportsCache<>( resourceService::getResource, this::runDisposers );
        this.context = scriptValueFactory.getContext();
        final Map<String, Object> globalVariables = new HashMap<>( this.scriptSettings.getGlobalVariables() );
        globalVariables.put( "app", ProxyObject.fromMap( application.buildMap( HashMap::new ) ) );
        globalVariables.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
    }

    @Override
    public ScriptExports executeMain( final ResourceKey key )
    {
        if ( RunMode.isDev() )
        {
            exportsCache.expireCacheIfNeeded();
        }
        return doExecuteMain( key );
    }

    @Override
    public CompletableFuture<ScriptExports> executeMainAsync( final ResourceKey key )
    {
        if ( RunMode.isDev() )
        {
            exportsCache.expireCacheIfNeeded();
        }
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
    public ObjectConverter getObjectConverter()
    {
        return javascriptHelper.objectConverter();
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
        final Object exports = executeRequire( key );
        ScriptValue scriptValue = scriptValueFactory.newValue( exports );
        return new GraalScriptExports( context, key, scriptValue, exports );
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
            throw GraalErrorHelper.handleError( e );
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

            final GraalScriptFunctions functions = new GraalScriptFunctions( context, script, this );
            func.execute( functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module );
            return module.getMember( "exports" );
        }
        catch ( final Exception e )
        {
            throw GraalErrorHelper.handleError( e );
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
            throw GraalErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( script.getKey(), "Script execute failed: [" + script.getKey() + "]", e );
        }
    }
}
