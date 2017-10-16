package com.enonic.xp.admin.impl.rest.resource.widget;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;


public class WidgetDescriptorResourceTest
    extends AdminResourceTestSupport
{

    private WidgetDescriptorService widgetDescriptorService;

    @Override
    protected Object getResourceInstance()
    {
        this.widgetDescriptorService = Mockito.mock( WidgetDescriptorService.class );

        final WidgetDescriptorResource resource = new WidgetDescriptorResource();
        resource.setWidgetDescriptorService( widgetDescriptorService );

        return resource;
    }

    @Test
    public void test_get_widgets_by_interface()
        throws Exception
    {

        final WidgetDescriptor widgetDescriptor1 = WidgetDescriptor.create().
            displayName( "My widget" ).
            addInterface( "com.enonic.xp.my-interface" ).
            addInterface( "com.enonic.xp.my-interface-2" ).
            key( DescriptorKey.from( "myapp:my-widget" ) ).
            build();

        final WidgetDescriptor widgetDescriptor2 = WidgetDescriptor.create().
            displayName( "My second widget" ).
            key( DescriptorKey.from( "myapp:my-second-widget" ) ).
            addProperty( "key1", "value1" ).
            build();

        final Descriptors<WidgetDescriptor> widgetDescriptors = Descriptors.from( widgetDescriptor1, widgetDescriptor2 );

        Mockito.when( widgetDescriptorService.getAllowedByInterfaces( Mockito.any() ) ).thenReturn( widgetDescriptors );

        String jsonString = request().path( "widget/list/byinterfaces" ).entity( "[\"someInterfaceName\"]",
                                                                                 MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "get_widgets_by_interface.json", jsonString );

    }
}
