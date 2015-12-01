package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByApplicationCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByApplicationCommand>
{
    private ApplicationKey applicationKey;

    public LayoutDescriptors execute()
    {
        final Application application = this.applicationService.getApplication( this.applicationKey );
        return getDescriptorsFromApplication( application );
    }

    public GetLayoutDescriptorsByApplicationCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
