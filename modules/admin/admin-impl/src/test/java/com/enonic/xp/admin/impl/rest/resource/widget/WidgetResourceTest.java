package com.enonic.xp.admin.impl.rest.resource.widget;

import java.time.Instant;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;


public class WidgetResourceTest
    extends AdminResourceTestSupport
{

    private WidgetDescriptorService widgetDescriptorService;

    private ApplicationDescriptorService applicationDescriptorService;

    @Override
    protected Object getResourceInstance()
    {
        this.widgetDescriptorService = Mockito.mock( WidgetDescriptorService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );

        final WidgetResource resource = new WidgetResource();
        resource.setWidgetDescriptorService( widgetDescriptorService );
        resource.setApplicationDescriptorService( applicationDescriptorService );

        return resource;
    }

    private ApplicationDescriptor createApplicationDescriptor( final Icon icon )
    {
        return ApplicationDescriptor.create().
            key( ApplicationKey.from( "myapp" ) ).
            description( "My application description" ).
            icon( icon ).
            build();
    }

    private WidgetDescriptor createWidgetDescriptor( final Icon icon )
    {
        return WidgetDescriptor.create().
            displayName( "My widget" ).
            description( "My widget description" ).
            addInterface( "com.enonic.xp.my-interface" ).
            addInterface( "com.enonic.xp.my-interface-2" ).
            key( DescriptorKey.from( "myapp:my-widget" ) ).
            setIcon( icon ).
            build();
    }

    @Test
    public void test_get_widgets_by_interface()
        throws Exception
    {

        final WidgetDescriptor widgetDescriptor1 = createWidgetDescriptor( null );

        final WidgetDescriptor widgetDescriptor2 = WidgetDescriptor.create().
            displayName( "My second widget" ).
            description( "My second widget description" ).
            key( DescriptorKey.from( "myapp:my-second-widget" ) ).
            addProperty( "key1", "value1" ).
            build();

        final Descriptors<WidgetDescriptor> widgetDescriptors = Descriptors.from( widgetDescriptor1, widgetDescriptor2 );

        Mockito.when( widgetDescriptorService.getAllowedByInterfaces( Mockito.any() ) ).thenReturn( widgetDescriptors );

        String jsonString = request().path( "widget/list/byinterfaces" ).entity( "[\"someInterfaceName\"]",
                                                                                 MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "get_widgets_by_interface.json", jsonString );

    }

    @Test
    public void get_icon_default()
        throws Exception
    {
        String response = request().
            path( "widget/icon/myapp/my-widget" ).
            queryParam( "hash", "123" ).
            get().getDataAsString();

        String expected = (String) Response.ok( readFromFile( "widget.svg" ), "image/svg+xml" ).build().getEntity();

        assertEquals( expected, response );
    }

    @Test
    public void get_icon_application()
        throws Exception
    {
        final Icon icon = Icon.from( new byte[]{0, 1, 2}, "image/svg", Instant.now() );

        final ApplicationDescriptor appDescriptor = createApplicationDescriptor( icon );
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        byte[] response = request().
            path( "widget/icon/myapp/my-widget" ).
            queryParam( "hash", "123" ).
            get().getData();

        byte[] expected = icon.toByteArray();

        assertTrue( Arrays.equals( expected, response ) );
    }

    @Test
    public void get_icon()
        throws Exception
    {
        final Icon icon = Icon.from( new byte[]{0, 1, 2}, "image/svg", Instant.now() );

        final WidgetDescriptor widgetDescriptor = createWidgetDescriptor( icon );
        Mockito.when( this.widgetDescriptorService.getByKey( Mockito.isA( DescriptorKey.class ) ) ).thenReturn( widgetDescriptor );

        byte[] response = request().
            path( "widget/icon/myapp/my-widget" ).
            queryParam( "hash", "123" ).
            get().getData();

        byte[] expected = icon.toByteArray();

        assertTrue( Arrays.equals( expected, response ) );
    }
}
