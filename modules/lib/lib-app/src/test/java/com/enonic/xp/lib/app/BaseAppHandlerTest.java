package com.enonic.xp.lib.app;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseAppHandlerTest
    extends ScriptTestSupport
{
    protected ApplicationService applicationService;

    protected ApplicationDescriptorService applicationDescriptorService;


    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.applicationService = Mockito.mock( ApplicationService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );

        addService( ApplicationService.class, this.applicationService );
        addService( ApplicationDescriptorService.class, this.applicationDescriptorService );
    }
}
