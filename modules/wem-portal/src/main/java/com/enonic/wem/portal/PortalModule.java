package com.enonic.wem.portal;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.postprocess.PostProcessModule;
import com.enonic.wem.portal.rendering.RenderingModule;
import com.enonic.wem.portal.restlet.RestletModule;
import com.enonic.wem.portal.script.ScriptModule;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ScriptModule() );
        install( new PostProcessModule() );
        install( new RenderingModule() );
        install( new RestletModule() );
    }
}
