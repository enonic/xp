package com.enonic.xp.admin.impl.widget;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.widget.GetWidgetDescriptorsParams;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

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
    public void getWidgetDescriptors()
        throws Exception
    {
        final WidgetDescriptor desc1 = WidgetDescriptor.create().
            key( DescriptorKey.from( "app:a" ) ).
            addInterface( "com.enonic.xp.my-interface" ).
            build();

        final WidgetDescriptor desc2 = WidgetDescriptor.create().
            key( DescriptorKey.from( "app:b" ) ).
            addInterface( "com.enonic.xp.another-interface" ).
            build();

        final WidgetDescriptor desc3 = WidgetDescriptor.create().
            key( DescriptorKey.from( "app:c" ) ).
            addInterface( "com.enonic.xp.my-interface" ).
            setAllowedPrincipals( Collections.singleton( PrincipalKey.from( "role:system.user.admin" ) ) ).
            build();

        final Descriptors<WidgetDescriptor> real = Descriptors.from( desc1, desc2, desc3 );
        Mockito.when( this.descriptorService.getAll( WidgetDescriptor.class ) ).thenReturn( real );
        Mockito.when( this.descriptorService.get( WidgetDescriptor.class, DescriptorKey.from( "app:c" ) ) ).thenReturn( desc3 );

        final GetWidgetDescriptorsParams getAllWidgetsParams = GetWidgetDescriptorsParams.create().build();
        final Descriptors<WidgetDescriptor> result1 = this.service.getWidgetDescriptors( getAllWidgetsParams );
        assertEquals( 3, result1.getSize() );

        final GetWidgetDescriptorsParams getAllMyInterfaceWidgetsParams =
            GetWidgetDescriptorsParams.create().setInterfaceNames( Collections.singleton( "com.enonic.xp.my-interface" ) ).build();
        final Descriptors<WidgetDescriptor> result2 = this.service.getWidgetDescriptors( getAllMyInterfaceWidgetsParams );
        assertEquals( 2, result2.getSize() );

        final GetWidgetDescriptorsParams getAllAllowedMyInterfaceWidgetsParams = GetWidgetDescriptorsParams.create().
            setInterfaceNames( Collections.singleton( "com.enonic.xp.my-interface" ) ).
            setPrincipalKeys( Collections.singleton( PrincipalKey.from( "role:system.user.admin" ) ) ).
            build();
        final Descriptors<WidgetDescriptor> result3 = this.service.getWidgetDescriptors( getAllAllowedMyInterfaceWidgetsParams );
        assertEquals( 2, result3.getSize() );

        final GetWidgetDescriptorsParams getAllAllowedMyInterfaceWidgetsParams2 = GetWidgetDescriptorsParams.create().
            setInterfaceNames( Collections.singleton( "com.enonic.xp.my-interface" ) ).
            setPrincipalKeys( Collections.singleton( PrincipalKey.from( "role:system.user.app" ) ) ).
            build();
        final Descriptors<WidgetDescriptor> result4 = this.service.getWidgetDescriptors( getAllAllowedMyInterfaceWidgetsParams2 );
        assertEquals( 1, result4.getSize() );

        final WidgetDescriptor widgetDescriptor = this.service.getByKey( DescriptorKey.from( "app:c" ) );
        assertEquals( desc3, widgetDescriptor );
    }
}
