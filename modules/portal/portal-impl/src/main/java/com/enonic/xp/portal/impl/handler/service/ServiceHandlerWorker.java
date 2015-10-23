package com.enonic.xp.portal.impl.handler.service;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.handler.controller.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.Site;

final class ServiceHandlerWorker
    extends ControllerHandlerWorker
{
    protected ResourceKey scriptDir;

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public void execute()
        throws Exception
    {
        this.request.setApplicationKey( this.scriptDir.getApplicationKey() );

        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );

        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( this.scriptDir );
        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }
}
