package com.enonic.xp.admin.impl.widget;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;

import static org.junit.Assert.*;

public class WidgetDescriptorServiceTest
{
    private DescriptorService descriptorService;

    private WidgetDescriptorServiceImpl service;

    @Before
    public void setup()
    {
        this.descriptorService = Mockito.mock( DescriptorService.class );

        this.service = new WidgetDescriptorServiceImpl();
        this.service.setDescriptorService( this.descriptorService );
    }

    @Test
    public void getByInterface()
        throws Exception
    {
        final WidgetDescriptor desc1 = WidgetDescriptor.create().key( DescriptorKey.from( "app:a" ) ).
            addInterface( "com.enonic.xp.my-interface" ).build();

        final WidgetDescriptor desc2 = WidgetDescriptor.create().key( DescriptorKey.from( "app:b" ) ).
            addInterface( "com.enonic.xp.my-interface" ).build();

        final Descriptors<WidgetDescriptor> real = Descriptors.from( desc1, desc2 );
        Mockito.when( this.descriptorService.getAll( WidgetDescriptor.class ) ).thenReturn( real );

        final Descriptors<WidgetDescriptor> result1 = this.service.getByInterfaces( "com.enonic.xp.my-interface" );
        assertNotNull( result1 );
        assertEquals( 2, result1.getSize() );

        final Descriptors<WidgetDescriptor> result2 = this.service.getByInterfaces( "unknown" );
        assertNotNull( result2 );
        assertEquals( 0, result2.getSize() );
    }
}
