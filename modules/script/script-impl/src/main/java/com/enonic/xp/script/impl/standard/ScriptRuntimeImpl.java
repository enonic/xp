package com.enonic.xp.script.impl.standard;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.ScriptRuntimeInternal;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorManager;
import com.enonic.xp.script.impl.util.JsObjectConverter;
import com.enonic.xp.script.runtime.ScriptSettings;

final class ScriptRuntimeImpl
    implements ScriptRuntimeInternal
{
    private final ScriptExecutorManager executorManager;

    ScriptRuntimeImpl( final ApplicationService applicationService, final ResourceService resourceService,
                       final ScriptSettings scriptSettings )
    {
        this.executorManager = new ScriptExecutorManager( applicationService, resourceService, scriptSettings );
    }

    @Override
    public boolean hasScript( final ResourceKey script )
    {
        final ResourceService service = this.executorManager.getExecutor( script.getApplicationKey() ).getResourceService();
        return service.getResource( script ).exists();
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptExecutor executor = this.executorManager.getExecutor( script.getApplicationKey() );
        return executor.executeMain( script );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.executorManager.invalidate( key );
    }

    @Override
    public ScriptValue toScriptValue( final ResourceKey script, final Object value )
    {
        final ScriptExecutor executor = this.executorManager.getExecutor( script.getApplicationKey() );
        return executor.newScriptValue( value );
    }

    @Override
    public Object toNativeObject( final ResourceKey script, final Object value )
    {
        final ScriptExecutor executor = this.executorManager.getExecutor( script.getApplicationKey() );
        return new JsObjectConverter( executor.getJavascriptHelper() ).toJs( value );
    }

    @Override
    public void runDisposers( final ApplicationKey key )
    {
        this.executorManager.runDisposers( key );
    }
}
