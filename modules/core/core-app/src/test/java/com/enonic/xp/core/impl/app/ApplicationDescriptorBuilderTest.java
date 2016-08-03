package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.support.ResourceTestHelper;

public class ApplicationDescriptorBuilderTest
{
    private ResourceTestHelper resourceTestHelper;

    private static final String APP_DESCRIPTOR_FILENAME = "application.xml";

    private static final String APP_ICON_FILENAME = "application.svg";

    @Before
    public void setup()
        throws Exception
    {
        resourceTestHelper = new ResourceTestHelper( this );
    }

    @Test
    public void buildApplicationDescriptor()
        throws Exception
    {
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "myapplication" );
        final URL resourceIcon = resourceTestHelper.getTestResource( APP_ICON_FILENAME );
        Mockito.when( bundle.getResource( APP_ICON_FILENAME ) ).thenReturn( resourceIcon );
        Mockito.when( bundle.getEntry( APP_ICON_FILENAME ) ).thenReturn( resourceIcon );

        final ApplicationDescriptor appDescriptor = new ApplicationDescriptorBuilder().
            bundle( bundle ).build();

        Assert.assertEquals( "My app description", appDescriptor.getDescription() );
    }
}