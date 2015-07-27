package com.enonic.xp.core.impl.content.page.part;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.PartDescriptors;

public class PartDescriptorServiceImpl_getByApplicationsTest
    extends AbstractPartDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Application application = createApplication( "foomodule" );
        createDescriptors( "foomodule:foomodule-part-descr" );

        mockResources( application, "/site/parts", "*", false, "site/parts/foomodule-part-descr" );
        final PartDescriptors result = this.service.getByModule( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Applications applications = createApplications( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-part-descr", "barmodule:barmodule-part-descr" );

        mockResources( applications.getModule( ApplicationKey.from( "foomodule" ) ), "/site/parts", "*", false,
                       "site/parts/foomodule-part-descr" );
        mockResources( applications.getModule( ApplicationKey.from( "barmodule" ) ), "/site/parts", "*", false,
                       "site/parts/barmodule-part-descr" );

        final PartDescriptors result = this.service.getByModules( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
