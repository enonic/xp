package com.enonic.xp.admin.impl.portal.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminExtensionIconResolverTest
{
    private AdminExtensionIconResolver instance;

    private ApplicationDescriptorService applicationDescriptorService;

    @BeforeEach
    void setUp()
    {
        this.applicationDescriptorService = mock( ApplicationDescriptorService.class );
        this.instance = new AdminExtensionIconResolver( this.applicationDescriptorService );
    }

    @Test
    void testResolveGetIconFromExtension()
    {
        final Icon icon = mock( Icon.class );

        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.getIcon() ).thenReturn( icon );

        assertEquals( icon, this.instance.resolve( descriptor ) );
    }

    @Test
    void testResolveGetIconFromApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.getIcon() ).thenReturn( null );
        when( descriptor.getApplicationKey() ).thenReturn( applicationKey );

        final Icon icon = mock( Icon.class );

        final ApplicationDescriptor appDescriptor = mock( ApplicationDescriptor.class );
        when( appDescriptor.getIcon() ).thenReturn( icon );
        when( this.applicationDescriptorService.get( applicationKey ) ).thenReturn( appDescriptor );

        assertEquals( icon, this.instance.resolve( descriptor ) );
    }

    @Test
    void testResolveDefaultIcon()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.getIcon() ).thenReturn( null );
        when( descriptor.getApplicationKey() ).thenReturn( applicationKey );

        when( this.applicationDescriptorService.get( eq( applicationKey ) ) ).thenReturn( null );

        assertNotNull( this.instance.resolve( descriptor ) );
    }

}
