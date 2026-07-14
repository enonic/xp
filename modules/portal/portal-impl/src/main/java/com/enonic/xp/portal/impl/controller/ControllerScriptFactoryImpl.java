package com.enonic.xp.portal.impl.controller;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.main.AppBootstrapBarrier;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class ControllerScriptFactoryImpl
    implements ControllerScriptFactory
{
    private final PortalScriptService scriptService;

    private final AppBootstrapBarrier bootstrapBarrier;

    @Activate
    public ControllerScriptFactoryImpl( @Reference final PortalScriptService scriptService,
                                        @Reference final AppBootstrapBarrier bootstrapBarrier )
    {
        this.scriptService = scriptService;
        this.bootstrapBarrier = bootstrapBarrier;
    }

    @Override
    public ControllerScript fromScript( final ResourceKey script )
    {
        // controllers observe a fully bootstrapped application: main.js completes first (#7821)
        this.bootstrapBarrier.await( script.getApplicationKey() );

        final ScriptExports exports = this.scriptService.execute( script );
        return new ControllerScriptImpl( exports );
    }
}
