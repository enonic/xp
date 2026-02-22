package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.AppNotRegisteredException;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.runtime.ScriptRuntime;

public class ScriptRuntimeImpl
    implements ScriptRuntime
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeImpl.class );

    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors = new ConcurrentHashMap<>();

    private final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory;

    public ScriptRuntimeImpl( final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory )
    {
        this.scriptExecutorFactory = scriptExecutorFactory;
    }

    @Override
    public boolean hasScript( final ResourceKey script )
    {
        final ScriptExecutor executor;
        try
        {
            executor = getExecutor( script.getApplicationKey() );
        }
        catch ( AppNotRegisteredException e )
        {
            return false;
        }

        return executor.getResourceService().getResource( script ).exists();
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptExecutor executor = getExecutor( script.getApplicationKey() );
        return executor.executeMain( script );
    }

    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        final ScriptExecutor executor = getExecutor( script.getApplicationKey() );
        return executor.executeMainAsync( script );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        LOG.debug( "Remove Script Executor for {}", key );
        final ScriptExecutor scriptExecutor = executors.remove( key );
        if ( scriptExecutor instanceof Closeable )
        {
            try
            {
                ( (Closeable) scriptExecutor ).close();
            }
            catch ( IOException e )
            {
                LOG.warn( "Could not close Script Executor for {}", key, e );
            }
        }
    }

    @Override
    public ScriptValue toScriptValue( final ResourceKey script, final Object value )
    {
        final ScriptExecutor executor = getExecutor( script.getApplicationKey() );
        return executor.newScriptValue( value );
    }

    @Override
    public Object toNativeObject( final ResourceKey script, final Object value )
    {
        final ScriptExecutor executor = getExecutor( script.getApplicationKey() );
        return executor.getObjectConverter().toJs( value );
    }

    public void runDisposers( final ApplicationKey key )
    {
        final ScriptExecutor executor = executors.get( key );
        if ( executor != null )
        {
            LOG.debug( "Run script disposers for {}", key );
            try
            {
                executor.runDisposers();
            }
            catch ( Exception e )
            {
                LOG.warn( "Error while running disposers", e );
            }
        }
    }

    private ScriptExecutor getExecutor( final ApplicationKey key )
    {
        return executors.computeIfAbsent( key, scriptExecutorFactory );
    }
}
