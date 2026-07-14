package com.enonic.xp.portal.impl.idprovider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.impl.main.AppBootstrapBarrier;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class IdProviderControllerScriptFactoryImpl
    implements IdProviderControllerScriptFactory
{
    private final PortalScriptService scriptService;

    private final AppBootstrapBarrier bootstrapBarrier;

    @Activate
    public IdProviderControllerScriptFactoryImpl( @Reference final PortalScriptService scriptService,
                                                  @Reference final AppBootstrapBarrier bootstrapBarrier )
    {
        this.scriptService = scriptService;
        this.bootstrapBarrier = bootstrapBarrier;
    }

    @Override
    public IdProviderControllerScript fromScript( final ResourceKey script )
    {
        // controllers observe a fully bootstrapped application: main.js completes first (#7821)
        this.bootstrapBarrier.await( script.getApplicationKey() );

        final ScriptExports exports = this.scriptService.execute( script );
        return new IdProviderControllerScriptImpl( exports );
    }
}
