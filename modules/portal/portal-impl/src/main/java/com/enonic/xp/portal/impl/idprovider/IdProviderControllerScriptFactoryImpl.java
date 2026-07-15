package com.enonic.xp.portal.impl.idprovider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class IdProviderControllerScriptFactoryImpl
    implements IdProviderControllerScriptFactory
{
    private final PortalScriptService scriptService;

    @Activate
    public IdProviderControllerScriptFactoryImpl( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public IdProviderControllerScript fromScript( final ResourceKey script )
    {
        // PortalScriptService gates the execution on the application's bootstrap (#7821)
        final ScriptExports exports = this.scriptService.execute( script );
        return new IdProviderControllerScriptImpl( exports );
    }
}
