package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.PageDescriptors;

final class GetPageDescriptorsByApplicationCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorsByApplicationCommand>
{
    private ApplicationKey applicationKey;

    private ApplicationService applicationService;

    public PageDescriptors execute()
    {
        final Application application = this.applicationService.getApplication( this.applicationKey );
        return getDescriptorsFromApplication( application );
    }

    public GetPageDescriptorsByApplicationCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }

    public final GetPageDescriptorsByApplicationCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }
}