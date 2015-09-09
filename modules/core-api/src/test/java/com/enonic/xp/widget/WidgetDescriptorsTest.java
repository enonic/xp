package com.enonic.xp.widget;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;

public class WidgetDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( WidgetDescriptors.empty().isEmpty() );
    }

    @Test
    public void from()
    {

        final WidgetDescriptor widgetDescriptor1 = WidgetDescriptor.create().
            displayName( "My widget" ).
            addInterface( "com.enonic.xp.my-interface" ).
            addInterface( "com.enonic.xp.my-interface-2" ).
            key( DescriptorKey.from( "module:my-widget" ) ).
            build();

        final WidgetDescriptor widgetDescriptor2 = WidgetDescriptor.create().
            displayName( "My second widget" ).
            key( DescriptorKey.from( "module:my-second-widget" ) ).
            build();

        assertEquals( 2, WidgetDescriptors.from( widgetDescriptor1, widgetDescriptor2 ).getSize() );
        assertEquals( 2, WidgetDescriptors.from( WidgetDescriptors.from( widgetDescriptor1, widgetDescriptor2 ) ).getSize() );
        assertEquals( 2, WidgetDescriptors.from( Arrays.asList( widgetDescriptor1, widgetDescriptor2 ) ).getSize() );
    }

}
