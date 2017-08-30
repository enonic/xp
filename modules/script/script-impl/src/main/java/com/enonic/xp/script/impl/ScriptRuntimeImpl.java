package com.enonic.xp.script.impl;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorManager;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptSettings;

final class ScriptRuntimeImpl
    implements ScriptRuntime
{
    private final ScriptExecutorManager executorManager;

    ScriptRuntimeImpl()
    {
        this.executorManager = new ScriptExecutorManager();
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

    void setApplicationService( final ApplicationService applicationService )
    {
        this.executorManager.setApplicationService( applicationService );
    }

    void setResourceService( final ResourceService resourceService )
    {
        this.executorManager.setResourceService( resourceService );
    }

    void setScriptSettings( final ScriptSettings scriptSettings )
    {
        this.executorManager.setScriptSettings( scriptSettings );
    }
}
