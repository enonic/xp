package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationDescriptorBuilderTest
{
    private ResourceTestHelper resourceTestHelper;

    private static final String APP_DESCRIPTOR_PATH_YML = "application.yml";

    private static final String APP_DESCRIPTOR_PATH_YAML = "application.yaml";

    private static final String APP_ICON_FILENAME = "application.svg";

    @BeforeEach
    void setup()
    {
        resourceTestHelper = new ResourceTestHelper( this );
    }

    @Test
    void buildApplicationDescriptor()
    {
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YML );
        Bundle bundle = mock( Bundle.class );
        when( bundle.getResource( APP_DESCRIPTOR_PATH_YML ) ).thenReturn( resource );
        when( bundle.getSymbolicName() ).thenReturn( "myapplication" );
        final URL resourceIcon = resourceTestHelper.getTestResource( APP_ICON_FILENAME );
        when( bundle.getResource( APP_ICON_FILENAME ) ).thenReturn( resourceIcon );
        when( bundle.getEntry( APP_ICON_FILENAME ) ).thenReturn( resourceIcon );

        final ApplicationDescriptor appDescriptor = new ApplicationDescriptorBuilder().bundle( bundle ).build();

        assertEquals( "My app description", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorWithYamlExtension()
    {
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YAML );
        Bundle bundle = mock( Bundle.class );
        when( bundle.getEntry( APP_DESCRIPTOR_PATH_YAML ) ).thenReturn( resource );
        when( bundle.getResource( APP_DESCRIPTOR_PATH_YAML ) ).thenReturn( resource );
        when( bundle.getSymbolicName() ).thenReturn( "myapplication" );

        final ApplicationDescriptor appDescriptor = new ApplicationDescriptorBuilder().bundle( bundle ).build();

        assertEquals( "My app description yaml", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorYamlTakesPriorityOverYml()
    {
        final URL yamlResource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YAML );
        final URL ymlResource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YML );
        Bundle bundle = mock( Bundle.class );
        when( bundle.getEntry( APP_DESCRIPTOR_PATH_YAML ) ).thenReturn( yamlResource );
        when( bundle.getResource( APP_DESCRIPTOR_PATH_YAML ) ).thenReturn( yamlResource );
        when( bundle.getResource( APP_DESCRIPTOR_PATH_YML ) ).thenReturn( ymlResource );
        when( bundle.getSymbolicName() ).thenReturn( "myapplication" );

        final ApplicationDescriptor appDescriptor = new ApplicationDescriptorBuilder().bundle( bundle ).build();

        assertEquals( "My app description yaml", appDescriptor.getDescription() );
    }

    @Test
    void hasAppDescriptorWithYamlExtension()
    {
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YAML );
        Bundle bundle = mock( Bundle.class );
        when( bundle.getEntry( APP_DESCRIPTOR_PATH_YAML ) ).thenReturn( resource );

        assertTrue( ApplicationDescriptorBuilder.hasAppDescriptor( bundle ) );
    }

    @Test
    void hasAppDescriptorWithYmlExtension()
    {
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_PATH_YML );
        Bundle bundle = mock( Bundle.class );
        when( bundle.getEntry( APP_DESCRIPTOR_PATH_YML ) ).thenReturn( resource );

        assertTrue( ApplicationDescriptorBuilder.hasAppDescriptor( bundle ) );
    }

    @Test
    void hasAppDescriptorWhenNeitherExists()
    {
        Bundle bundle = mock( Bundle.class );

        assertFalse( ApplicationDescriptorBuilder.hasAppDescriptor( bundle ) );
    }
}
