package com.enonic.xp.script.impl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, service = {ScriptRuntimeFactory.class, ApplicationInvalidator.class})
public final class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeFactory, ApplicationInvalidator
{
    private final List<ScriptRuntime> list;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public ScriptRuntimeFactoryImpl()
    {
        this.list = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl();
        runtime.setScriptSettings( settings );
        runtime.setApplicationService( this.applicationService );
        runtime.setResourceService( this.resourceService );

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
