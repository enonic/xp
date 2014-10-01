package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.portal.internal.ResourceProvider;

public final class PublicResourceProvider
    implements ResourceProvider<PublicResource2>
{
    private ModuleService moduleService;

    @Override
    public Class<PublicResource2> getType()
    {
        return PublicResource2.class;
    }

    @Override
    public PublicResource2 newResource()
    {
        final PublicResource2 instance = new PublicResource2();
        instance.moduleService = this.moduleService;
        return instance;
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
