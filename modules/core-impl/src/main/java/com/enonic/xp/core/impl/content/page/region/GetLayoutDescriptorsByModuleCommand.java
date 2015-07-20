package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByModuleCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModuleCommand>
{
    private ApplicationKey applicationKey;

    public LayoutDescriptors execute()
    {
        final Application application = this.applicationService.getModule( this.applicationKey );
        return getDescriptorsFromModule( application );
    }

    public GetLayoutDescriptorsByModuleCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
