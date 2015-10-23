package com.enonic.xp.admin.impl.rest.resource.widget.json;


import org.junit.Test;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;

public class WidgetDescriptorJsonTest
{

    @Test
    public void testJsonObjectCreation()
    {
        final WidgetDescriptor widgetDescriptor = WidgetDescriptor.create().
            displayName( "My widget" ).
            addInterface( "com.enonic.xp.my-interface" ).
            addInterface( "com.enonic.xp.my-interface-2" ).
            key( DescriptorKey.from( "myapp:my-widget" ) ).
            build();

        final WidgetDescriptorJson json = new WidgetDescriptorJson( widgetDescriptor );

        assertEquals( "myapp:my-widget", json.getKey() );
        assertEquals( "_/widgets/myapp/my-widget", json.getUrl() );
        assertEquals( "My widget", json.getDisplayName() );
        assertEquals( 2, json.getInterfaces().size() );
    }
}
