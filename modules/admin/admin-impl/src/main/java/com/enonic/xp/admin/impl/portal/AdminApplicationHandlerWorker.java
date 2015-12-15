package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.HttpStatus;

final class AdminApplicationHandlerWorker
    extends ControllerHandlerWorker
{
    protected ResourceKey scriptDir;

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public void execute()
        throws Exception
    {

        if ( this.request.getMode() != RenderMode.ADMIN )
        {
            throw new PortalException( HttpStatus.FORBIDDEN, "Render mode must be ADMIN." );
        }

        this.request.setApplicationKey( this.scriptDir.getApplicationKey() );

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( this.scriptDir );
        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }
}
