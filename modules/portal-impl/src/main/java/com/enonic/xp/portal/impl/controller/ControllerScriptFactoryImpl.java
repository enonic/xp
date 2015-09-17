package com.enonic.xp.portal.impl.controller;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class ControllerScriptFactoryImpl
    implements ControllerScriptFactory
{
    private PortalScriptService scriptService;

    private PostProcessor postProcessor;

    @Override
    public ControllerScript fromDir( final ResourceKey dir )
    {
        return fromScript( dir.resolve( dir.getName() + ".js" ) );
    }

    @Override
    public ControllerScript fromScript( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new ControllerScriptImpl( exports, this.postProcessor, this.scriptService );
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
