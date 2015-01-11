package com.enonic.xp.portal.impl.resource.service;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.controller.ControllerScript;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.xp.portal.impl.resource.controller.ControllerResource;

public final class ServiceControllerResource
    extends ControllerResource
{
    protected ResourceKey scriptDir;

    @Override
    protected void configure( final PortalContextImpl context )
    {
        // Do nothing
    }

    @Override
    protected RenderResult execute( final PortalContextImpl context )
        throws Exception
    {
        final ControllerScript controllerScript = this.services.getControllerScriptFactory().newController( this.scriptDir );
        controllerScript.execute( context );

        return new PortalResponseSerializer( context.getResponse() ).serialize();
    }
}
