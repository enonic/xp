package com.enonic.xp.portal.impl.error;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class ErrorHandlerScriptFactoryImpl
    implements ErrorHandlerScriptFactory
{
    private final PortalScriptService scriptService;

    @Activate
    public ErrorHandlerScriptFactoryImpl( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public ErrorHandlerScript errorScript( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new ErrorHandlerScriptImpl( exports );
    }
}
