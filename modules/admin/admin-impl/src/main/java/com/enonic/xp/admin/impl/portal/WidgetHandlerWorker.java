package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;

final class WidgetHandlerWorker
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

        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );

        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( this.scriptDir );
        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }
}
