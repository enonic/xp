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
    public void getDescriptorsFromSingleApplication()
        throws Exception
    {
        final Application application = createApplication( "fooapplication" );
        createDescriptors( "fooapplication:fooapplication-part-descr" );

        mockResources( application, "/site/parts", "*", false, "site/parts/fooapplication-part-descr" );
        final PartDescriptors result = this.service.getByApplication( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleApplications()
        throws Exception
    {
        final Applications applications = createApplications( "fooapplication", "barapplication" );
        createDescriptors( "fooapplication:fooapplication-part-descr", "barapplication:barapplication-part-descr" );

        mockResources( applications.getApplication( ApplicationKey.from( "fooapplication" ) ), "/site/parts", "*", false,
                       "site/parts/fooapplication-part-descr" );
        mockResources( applications.getApplication( ApplicationKey.from( "barapplication" ) ), "/site/parts", "*", false,
                       "site/parts/barapplication-part-descr" );

        final PartDescriptors result = this.service.getByApplications( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
