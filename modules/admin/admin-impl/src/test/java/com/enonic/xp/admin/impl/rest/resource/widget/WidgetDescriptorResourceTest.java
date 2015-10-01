package com.enonic.xp.admin.impl.rest.resource.widget;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptors;
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
            key( DescriptorKey.from( "module:my-widget" ) ).
            build();

        final WidgetDescriptor widgetDescriptor2 = WidgetDescriptor.create().
            displayName( "My second widget" ).
            key( DescriptorKey.from( "module:my-second-widget" ) ).
            build();

        WidgetDescriptors widgetDescriptors = WidgetDescriptors.from( widgetDescriptor1, widgetDescriptor2 );

        Mockito.when( widgetDescriptorService.getByInterface( Mockito.any() ) ).thenReturn( widgetDescriptors );

        String jsonString =
            request().path( "widget/descriptor/byInterface" ).queryParam( "interface", "someInterfaceName" ).get().getAsString();

        assertJson( "get_widgets_by_interface.json", jsonString );
    }
}
