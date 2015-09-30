package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.LayoutDescriptors;

public class LayoutDescriptorServiceImpl_getByApplicationsTest
    extends AbstractLayoutDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleApplication()
        throws Exception
    {
        final Application application = createApplication( "fooapplication" );
        createDescriptors( "fooapplication:fooapplication-layout-descr" );

        mockResources( application, "/site/layouts", "site/layouts/fooapplication-layout-descr/" );

        final LayoutDescriptors result = this.service.getByApplication( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleApplications()
        throws Exception
    {
        final Applications applications = createApplications( "fooapplication", "barapplication" );
        createDescriptors( "fooapplication:fooapplication-layout-descr", "barapplication:barapplication-layout-descr" );

        mockResources( applications.getApplication( ApplicationKey.from( "fooapplication" ) ), "/site/layouts",
                       "site/layouts/fooapplication-layout-descr/" );
        mockResources( applications.getApplication( ApplicationKey.from( "barapplication" ) ), "/site/layouts",
                       "site/layouts/barapplication-layout-descr/" );

        final LayoutDescriptors result = this.service.getByApplications( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
