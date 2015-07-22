package com.enonic.xp.core.impl.site;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.support.ResourceTestHelper;

public class SiteDescriptorBuilderTest
{
    private ResourceTestHelper resourceTestHelper;

    private static final String TEST_SITE_DESCRIPTOR_FILENAME = "site.xml";

    private static final String SITE_DESCRIPTOR_FILENAME = "site/site.xml";

    @Before
    public void setup()
        throws Exception
    {
        resourceTestHelper = new ResourceTestHelper( this );
    }

    @Test
    public void build_site_descriptor()
        throws Exception
    {
        final URL resource = resourceTestHelper.getTestResource( TEST_SITE_DESCRIPTOR_FILENAME );
        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( SITE_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "mymodule" );

        SiteDescriptorBuilder builder = new SiteDescriptorBuilder().
            bundle( bundle );

        final SiteDescriptor siteDescriptor = builder.build();

        Assert.assertEquals( 1, siteDescriptor.getForm().getFormItems().size() );
        Assert.assertEquals( 2, siteDescriptor.getMetaSteps().getSize() );
    }
}
