package com.enonic.xp.admin.impl.rest.resource.macro;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

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

    @Override
    protected Object getResourceInstance()
    {
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );

        final MacroResource resource = new MacroResource();
        resource.setMacroDescriptorService( this.macroDescriptorService );

        return resource;
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
    {
        final Form config = Form.create().build();

        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my-app1:macro1" ) ).
            description( "my description" ).
            displayName( "my macro1 name" ).
            form( config ).
            icon( Icon.from( new byte[]{123}, "image/png", Instant.now() ) ).
            build();

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.create().
            key( MacroKey.from( "my-app2:macro2" ) ).
            description( "my description" ).
            displayName( "my macro2 name" ).
            form( config ).
            icon( Icon.from( new byte[]{123}, "image/png", Instant.now() ) ).
            build();

        return MacroDescriptors.from( macroDescriptor1, macroDescriptor2 );
    }
}
