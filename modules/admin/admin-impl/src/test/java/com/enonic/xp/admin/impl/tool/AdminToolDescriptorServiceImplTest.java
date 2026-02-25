package com.enonic.xp.admin.impl.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminToolDescriptorServiceImplTest
{
    private DescriptorService descriptorService;

    private AdminToolDescriptorServiceImpl service;

    @BeforeEach
    void setUp()
    {
        this.descriptorService = mock( DescriptorService.class );
        this.service = new AdminToolDescriptorServiceImpl( this.descriptorService );
    }

    @Test
    void getByApplication()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp1" );
        final AdminToolDescriptor descriptor =
            AdminToolDescriptor.create().key( DescriptorKey.from( appKey, "myadmintool" ) ).displayName( "My admin tool" ).build();

        when( this.descriptorService.get( AdminToolDescriptor.class, ApplicationKeys.from( appKey ) ) ).thenReturn(
            Descriptors.from( descriptor ) );

        final AdminToolDescriptors result = this.service.getByApplication( appKey );

        assertEquals( 1, result.getSize() );
        assertSame( descriptor, result.get( 0 ) );
    }

    @Test
    void getByKey()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myadmintool" );
        final AdminToolDescriptor descriptor =
            AdminToolDescriptor.create().key( descriptorKey ).displayName( "My admin tool" ).build();

        when( this.descriptorService.get( AdminToolDescriptor.class, descriptorKey ) ).thenReturn( descriptor );

        final AdminToolDescriptor result = this.service.getByKey( descriptorKey );

        assertNotNull( result );
        assertSame( descriptor, result );
    }

    @Test
    void getAll()
    {
        final AdminToolDescriptor descriptor1 =
            AdminToolDescriptor.create().key( DescriptorKey.from( "myapp1:tool1" ) ).displayName( "Tool 1" ).build();
        final AdminToolDescriptor descriptor2 =
            AdminToolDescriptor.create().key( DescriptorKey.from( "myapp2:tool2" ) ).displayName( "Tool 2" ).build();

        when( this.descriptorService.getAll( AdminToolDescriptor.class ) ).thenReturn(
            Descriptors.from( descriptor1, descriptor2 ) );

        final AdminToolDescriptors result = this.service.getAll();

        assertEquals( 2, result.getSize() );
        assertSame( descriptor1, result.get( 0 ) );
        assertSame( descriptor2, result.get( 1 ) );
    }
}
