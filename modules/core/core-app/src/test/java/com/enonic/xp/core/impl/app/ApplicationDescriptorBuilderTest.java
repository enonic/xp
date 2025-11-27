package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationDescriptorBuilderTest
{
    private ResourceTestHelper resourceTestHelper;

    private static final String APP_DESCRIPTOR_FILENAME = "application.yml";

    private static final String APP_ICON_FILENAME = "application.svg";

    @BeforeEach
    void setup()
    {
        resourceTestHelper = new ResourceTestHelper( this );
    }

    @Test
    void buildApplicationDescriptor()
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

        assertEquals( "My app description", appDescriptor.getDescription() );
    }
}
