package com.enonic.xp.script.impl.standard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.ScriptRuntimeInternal;
import com.enonic.xp.script.impl.ScriptRuntimeProvider;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, property = "provider=standard", service = {ScriptRuntimeProvider.class})
public final class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeProvider, ScriptRuntimeFactory
{
    private final List<ScriptRuntimeInternal> list = new CopyOnWriteArrayList<>();

    private ApplicationService applicationService;

    private ResourceService resourceService;

    @Override
    public ScriptRuntimeInternal create( final ScriptSettings settings )
    {
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( applicationService, resourceService, settings );

        this.list.add( runtime );
        return runtime;
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.list.remove( runtime );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.list.forEach( runtime -> runtime.invalidate( key ) );
    }

    @Override
    public void runDisposers( final ApplicationKey key )
    {
        this.list.forEach( runtime -> runtime.runDisposers( key ) );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
