package com.enonic.xp.admin.impl.widget;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.widget.WidgetDescriptor;
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

class WidgetDescriptorServiceTest
{
    private DescriptorService descriptorService;

    private WidgetDescriptorServiceImpl service;

    private WidgetDescriptor widgetDescriptor1;

    private WidgetDescriptor widgetDescriptor2;

    private WidgetDescriptor widgetDescriptor3;

    private WidgetDescriptor widgetDescriptor4;

    private WidgetDescriptor widgetDescriptor5;

    @BeforeEach
    void setup()
    {
        this.descriptorService = Mockito.mock( DescriptorService.class );

        this.service = new WidgetDescriptorServiceImpl( this.descriptorService );

        widgetDescriptor1 =
            WidgetDescriptor.create().key( DescriptorKey.from( "app:a" ) ).addInterface( "com.enonic.xp.my-interface" ).build();

        widgetDescriptor2 =
            WidgetDescriptor.create().key( DescriptorKey.from( "app:b" ) ).addInterface( "com.enonic.xp.another-interface" ).build();

        widgetDescriptor3 = WidgetDescriptor.create()
            .key( DescriptorKey.from( "app:c" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.singleton( PrincipalKey.from( "role:system.user.admin" ) ) ) )
            .build();

        widgetDescriptor4 = WidgetDescriptor.create()
            .key( DescriptorKey.from( "app:d" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.singleton( PrincipalKey.from( "user:system:anonymous" ) ) ) )
            .build();

        widgetDescriptor5 = WidgetDescriptor.create()
            .key( DescriptorKey.from( "app:e" ) )
            .addInterface( "com.enonic.xp.my-interface" )
            .allowedPrincipals( PrincipalKeys.from( Collections.emptyList() ) )
            .build();

        final Descriptors<WidgetDescriptor> widgetDescriptors =
            Descriptors.from( widgetDescriptor1, widgetDescriptor2, widgetDescriptor3, widgetDescriptor4, widgetDescriptor5 );
        Mockito.when( this.descriptorService.getAll( WidgetDescriptor.class ) ).thenReturn( widgetDescriptors );
        Mockito.when( this.descriptorService.get( WidgetDescriptor.class, DescriptorKey.from( "app:c" ) ) ).thenReturn( widgetDescriptor3 );
        Mockito.when( this.descriptorService.get( WidgetDescriptor.class, DescriptorKey.from( "app:d" ) ) ).thenReturn( widgetDescriptor4 );
        Mockito.when( this.descriptorService.get( WidgetDescriptor.class, ApplicationKeys.from( "app" ) ) )
            .thenReturn( Descriptors.from( widgetDescriptor3, widgetDescriptor4 ) );
    }

    @Test
    void get_by_application()
    {
        final Descriptors<WidgetDescriptor> result = this.service.getByApplication( ApplicationKey.from( "app" ) );

        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( widgetDescriptor3 ) );
        assertTrue( result.contains( widgetDescriptor4 ) );
    }

    @Test
    void get_by_interfaces()
    {
        final Descriptors<WidgetDescriptor> result = this.service.getByInterfaces( "com.enonic.xp.my-interface" );
        assertEquals( 4, result.getSize() );
        assertTrue( result.contains( widgetDescriptor1 ) );
        assertTrue( result.contains( widgetDescriptor3 ) );
        assertTrue( result.contains( widgetDescriptor4 ) );
        assertTrue( result.contains( widgetDescriptor5 ) );
    }

    @Test
    void get_by_key()
    {
        final WidgetDescriptor allowedWidget = this.service.getByKey( DescriptorKey.from( "app:d" ) );
        assertSame( allowedWidget, widgetDescriptor4 );

        final WidgetDescriptor unknownWidget = this.service.getByKey( DescriptorKey.from( "app:unknown" ) );
        assertNull( unknownWidget );
    }
}
