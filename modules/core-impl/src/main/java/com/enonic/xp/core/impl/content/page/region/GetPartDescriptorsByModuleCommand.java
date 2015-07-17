package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByModuleCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModuleCommand>
{
    private ApplicationKey applicationKey;

    public PartDescriptors execute()
    {
        final Module module = this.moduleService.getModule( this.applicationKey );
        return getDescriptorsFromModule( module );
    }

    public GetPartDescriptorsByModuleCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
