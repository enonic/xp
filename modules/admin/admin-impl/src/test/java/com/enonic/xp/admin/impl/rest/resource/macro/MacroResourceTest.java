package com.enonic.xp.admin.impl.rest.resource.macro;

import java.time.Instant;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

import static org.junit.Assert.*;

public class MacroResourceTest
    extends AdminResourceTestSupport
{

    private MacroDescriptorService macroDescriptorService;

    private MacroResource macroResource;

    @Override
    protected Object getResourceInstance()
    {
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );

        this.macroResource = new MacroResource();
        macroResource.setMacroDescriptorService( this.macroDescriptorService );

        return macroResource;
    }

    @Test
    public void testGetDefaultIcon()
        throws Exception
    {
        String response = request().
            path( "macro/icon/key" ).
            get().getAsString();

        assertNotNull( response.getBytes() );
    }

    @Test
    public void testMacroIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "macro1.svg" ) );
        final Icon icon = Icon.from( data, "image/svg+xml", Instant.now() );

        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( "myapp:macro1" ) ).
            description( "my description" ).
            displayName( "my macro1 name" ).
            form( Form.create().build() ).
            icon( icon ).
            build();

        Mockito.when( macroDescriptorService.getByKey( macroDescriptor.getKey() ) ).thenReturn( macroDescriptor );

        final Response response = this.macroResource.getIcon( "myapp:macro1", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( icon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, (byte[]) response.getEntity() );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        Mockito.when( this.macroDescriptorService.getAll() ).thenReturn( this.getTestDescriptors() );

        String response = request().
            path( "macro/list" ).
            get().getAsString();
        assertJson( "get_all_macros.json", response );
    }

    private MacroDescriptors getTestDescriptors()
        throws Exception
    {
        final Form config = Form.create().build();

        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my-app1:macro1" ) ).
            description( "my description" ).
            displayName( "my macro1 name" ).
            form( config ).
            build();

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.create().
            key( MacroKey.from( "my-app2:macro2" ) ).
            description( "my description" ).
            displayName( "my macro2 name" ).
            form( config ).
            build();

        return MacroDescriptors.from( macroDescriptor1, macroDescriptor2 );
    }
}
