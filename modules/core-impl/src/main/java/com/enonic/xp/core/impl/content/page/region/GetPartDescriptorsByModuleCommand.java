package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByModuleCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModuleCommand>
{
    private ApplicationKey applicationKey;

    public PartDescriptors execute()
    {
        final Application application = this.applicationService.getModule( this.applicationKey );
        return getDescriptorsFromModule( application );
    }

    public GetPartDescriptorsByModuleCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
