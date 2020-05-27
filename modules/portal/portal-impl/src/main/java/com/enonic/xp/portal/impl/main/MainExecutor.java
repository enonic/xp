package com.enonic.xp.portal.impl.main;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

@Component(immediate = true)
public final class MainExecutor
    implements ApplicationListener
{
    private final PortalScriptService scriptService;

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public void activated( final Application app )
    {
        executeMain( ResourceKey.from( app.getKey(), "/main.js" ) );
    }

    @Override
    public void deactivated( final Application app )
    {
    }

    private void executeMain( final ResourceKey key )
    {
        if ( this.scriptService.hasScript( key ) )
        {
            this.scriptService.executeAsync( key );
        }
    }
}
