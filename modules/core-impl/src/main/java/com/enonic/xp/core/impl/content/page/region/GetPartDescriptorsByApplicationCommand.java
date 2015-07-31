package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByApplicationCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByApplicationCommand>
{
    private ApplicationKey applicationKey;

    public PartDescriptors execute()
    {
        final Application application = this.applicationService.getApplication( this.applicationKey );
        return getDescriptorsFromApplication( application );
    }

    public GetPartDescriptorsByApplicationCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }
}
