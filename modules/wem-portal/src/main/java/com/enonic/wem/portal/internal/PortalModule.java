package com.enonic.wem.portal.internal;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.internal.controller.ControllerModule;
import com.enonic.wem.portal.internal.postprocess.PostProcessModule;
import com.enonic.wem.portal.internal.rendering.RenderingModule;
import com.enonic.wem.portal.internal.restlet.RestletModule;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ControllerModule() );
        install( new PostProcessModule() );
        install( new RenderingModule() );
        install( new RestletModule() );
    }
}
