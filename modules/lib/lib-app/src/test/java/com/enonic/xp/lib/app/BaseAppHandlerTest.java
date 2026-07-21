package com.enonic.xp.lib.app;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseAppHandlerTest
    extends ScriptTestSupport
{
    protected ApplicationDescriptorService applicationDescriptorService;


    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );

        addService( ApplicationDescriptorService.class, this.applicationDescriptorService );
    }
}
