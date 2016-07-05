package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;

final class WidgetHandlerWorker
    extends ControllerHandlerWorker
{
    protected ResourceKey scriptDir;

    protected ControllerScriptFactory controllerScriptFactory;

    public WidgetHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {

        if ( this.request.getMode() != RenderMode.ADMIN )
        {
            throw WebException.forbidden( "Render mode must be ADMIN." );
        }

        this.request.setApplicationKey( this.scriptDir.getApplicationKey() );

        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );

        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( this.scriptDir );
        return controllerScript.execute( this.request );
    }
}
