package com.enonic.xp.admin.impl.portal.widget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WidgetIconResolverTest
{
    private WidgetIconResolver instance;

    private ApplicationDescriptorService applicationDescriptorService;

    @BeforeEach
    void setUp()
    {
        this.applicationDescriptorService = mock( ApplicationDescriptorService.class );
        this.instance = new WidgetIconResolver( this.applicationDescriptorService );
    }

    @Test
    void testResolveGetIconFromWidget()
    {
        final Icon icon = mock( Icon.class );

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.getIcon() ).thenReturn( icon );

        assertEquals( icon, this.instance.resolve( widgetDescriptor ) );
    }

    @Test
    void testResolveGetIconFromApp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.getIcon() ).thenReturn( null );
        when( widgetDescriptor.getApplicationKey() ).thenReturn( applicationKey );

        final Icon icon = mock( Icon.class );

        final ApplicationDescriptor appDescriptor = mock( ApplicationDescriptor.class );
        when( appDescriptor.getIcon() ).thenReturn( icon );
        when( this.applicationDescriptorService.get( applicationKey ) ).thenReturn( appDescriptor );

        assertEquals( icon, this.instance.resolve( widgetDescriptor ) );
    }

    @Test
    void testResolveDefaultIcon()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.getIcon() ).thenReturn( null );
        when( widgetDescriptor.getApplicationKey() ).thenReturn( applicationKey );

        when( this.applicationDescriptorService.get( eq( applicationKey ) ) ).thenReturn( null );

        assertNotNull( this.instance.resolve( widgetDescriptor ) );
    }

}
