package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.portal.internal.base.ModuleBaseResourceFactory;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;

public final class ServiceResourceFactory
    extends ModuleBaseResourceFactory<ServiceResource>
{
    private JsControllerFactory controllerFactory;

    public ServiceResourceFactory()
    {
        super( ServiceResource.class );
    }

    @Override
    protected void configure( final ServiceResource instance )
    {
        super.configure( instance );
        instance.controllerFactory = this.controllerFactory;
    }

    public void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }
}
