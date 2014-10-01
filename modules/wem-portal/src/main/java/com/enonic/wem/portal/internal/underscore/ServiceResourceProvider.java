package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.ResourceProvider;

public final class ServiceResourceProvider
    implements ResourceProvider<ServiceResource2>
{
    private ModuleService moduleService;

    private JsControllerFactory controllerFactory;

    @Override
    public Class<ServiceResource2> getType()
    {
        return ServiceResource2.class;
    }

    @Override
    public ServiceResource2 newResource()
    {
        final ServiceResource2 instance = new ServiceResource2();
        instance.moduleService = this.moduleService;
        instance.controllerFactory = this.controllerFactory;
        return instance;
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    public void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }
}
