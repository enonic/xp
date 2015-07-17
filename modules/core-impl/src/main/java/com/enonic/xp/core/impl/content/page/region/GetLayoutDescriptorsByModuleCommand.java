package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByModuleCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModuleCommand>
{
    private ApplicationKey applicationKey;

    public LayoutDescriptors execute()
    {
        final Module module = this.applicationService.getModule( this.applicationKey );
        return getDescriptorsFromModule( module );
    }

    public GetLayoutDescriptorsByModuleCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
