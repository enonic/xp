package com.enonic.xp.admin.impl.extension;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminExtensionDescriptorServiceTest
{
    private AdminExtensionDescriptorServiceImpl service;

    private AdminExtensionDescriptor descriptor1;

    private AdminExtensionDescriptor descriptor3;

    private AdminExtensionDescriptor descriptor4;

    private AdminExtensionDescriptor descriptor5;

    @BeforeEach
    void setup()
    {
        final DescriptorService descriptorService = Mockito.mock( DescriptorService.class );

        this.service = new AdminExtensionDescriptorServiceImpl( descriptorService );

        descriptor1 =
            AdminExtensionDescriptor.create().key( DescriptorKey.from( "app:a" ) ).addInterface( "com.enonic.xp.my-interface" ).build();

        final AdminExtensionDescriptor descriptor2 =
            AdminExtensionDescriptor.create().key( DescriptorKey.from( "app:b" ) ).addInterface( "com.enonic.xp.another-interface" ).build();

        descriptor3 = AdminExtensionDescriptor.create()
            .key( DescriptorKey.from( "app:c" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.singleton( PrincipalKey.from( "role:system.user.admin" ) ) ) )
            .build();

        descriptor4 = AdminExtensionDescriptor.create()
            .key( DescriptorKey.from( "app:d" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.singleton( PrincipalKey.from( "user:system:anonymous" ) ) ) )
            .build();

        descriptor5 = AdminExtensionDescriptor.create()
            .key( DescriptorKey.from( "app:e" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.emptyList() ) )
            .build();

        final Descriptors<AdminExtensionDescriptor> descriptors =
            Descriptors.from( descriptor1, descriptor2, descriptor3, descriptor4, descriptor5 );
        Mockito.when( descriptorService.getAll( AdminExtensionDescriptor.class ) ).thenReturn( descriptors );
        Mockito.when( descriptorService.get( AdminExtensionDescriptor.class, DescriptorKey.from( "app:c" ) ) ).thenReturn(
            descriptor3 );
        Mockito.when( descriptorService.get( AdminExtensionDescriptor.class, DescriptorKey.from( "app:d" ) ) ).thenReturn(
            descriptor4 );
        Mockito.when( descriptorService.get( AdminExtensionDescriptor.class, ApplicationKeys.from( "app" ) ) )
            .thenReturn( Descriptors.from( descriptor3, descriptor4 ) );
    }

    @Test
    void get_by_application()
    {
        final Descriptors<AdminExtensionDescriptor> result = this.service.getByApplication( ApplicationKey.from( "app" ) );

        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( descriptor3 ) );
        assertTrue( result.contains( descriptor4 ) );
    }

    @Test
    void get_by_interfaces()
    {
        final Descriptors<AdminExtensionDescriptor> result = this.service.getByInterfaces( "com.enonic.xp.my-interface" );
        assertEquals( 4, result.getSize() );
        assertTrue( result.contains( descriptor1 ) );
        assertTrue( result.contains( descriptor3 ) );
        assertTrue( result.contains( descriptor4 ) );
        assertTrue( result.contains( descriptor5 ) );
    }

    @Test
    void get_by_key()
    {
        final AdminExtensionDescriptor allowedExtension = this.service.getByKey( DescriptorKey.from( "app:d" ) );
        assertSame( allowedExtension, descriptor4 );

        final AdminExtensionDescriptor unknownExtension = this.service.getByKey( DescriptorKey.from( "app:unknown" ) );
        assertNull( unknownExtension );
    }
}
