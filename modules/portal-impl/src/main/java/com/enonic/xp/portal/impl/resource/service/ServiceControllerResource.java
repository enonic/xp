package com.enonic.xp.portal.impl.resource.service;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.resource.controller.ControllerResource;
import com.enonic.xp.resource.ResourceKey;

public final class ServiceControllerResource
    extends ControllerResource
{
    protected ResourceKey scriptDir;

    protected Content content;

    protected Site site;

    @Override
    protected void configure( final PortalRequest portalRequest )
    {
        portalRequest.setContent( this.content );
        portalRequest.setSite( this.site );
        portalRequest.setModule( this.scriptDir.getModule() );
    }

    @Override
    protected PortalResponse execute( final PortalRequest portalRequest )
        throws Exception
    {
        final ControllerScript controllerScript = this.services.getControllerScriptFactory().fromDir( this.scriptDir );
        final PortalResponse portalResponse = controllerScript.execute( portalRequest );

        return new PortalResponseSerializer( portalResponse ).serialize();
    }
}
