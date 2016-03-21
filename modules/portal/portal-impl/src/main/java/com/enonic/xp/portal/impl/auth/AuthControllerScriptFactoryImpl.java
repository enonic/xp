package com.enonic.xp.portal.impl.auth;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class AuthControllerScriptFactoryImpl
    implements AuthControllerScriptFactory
{
    private PortalScriptService scriptService;

    @Override
    public AuthControllerScript fromScript( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new AuthControllerScriptImpl( exports );
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

}
