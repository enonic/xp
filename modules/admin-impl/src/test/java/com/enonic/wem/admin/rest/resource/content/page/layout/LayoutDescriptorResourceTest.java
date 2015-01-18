package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.LayoutDescriptor;
import com.enonic.wem.api.content.page.region.LayoutDescriptorService;
import com.enonic.wem.api.content.page.region.LayoutDescriptors;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKeys;

import static com.enonic.wem.api.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.form.Input.newInput;

public class LayoutDescriptorResourceTest
    extends AbstractResourceTest
{
    private LayoutDescriptorService layoutDescriptorService;

    @Override
    protected Object getResourceInstance()
    {
        layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        final LayoutDescriptorResource resource = new LayoutDescriptorResource();
        resource.setLayoutDescriptorService( layoutDescriptorService );

        return resource;
    }

    @Test
    public void test_get_by_key()
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( "module:fancy-layout" );
        final Form layoutForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.newLayoutDescriptor().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "left" ).build() ).
                add( newRegionDescriptor().name( "right" ).build() ).
                build() ).
            key( key ).
            build();

        Mockito.when( layoutDescriptorService.getByKey( key ) ).thenReturn( layoutDescriptor );

        String jsonString = request().path( "content/page/layout/descriptor" ).
            queryParam( "key", "module:fancy-layout" ).get().getAsString();

        assertJson( "get_by_key_success.json", jsonString );
    }

    @Test
    public void test_get_by_modules()
        throws Exception
    {
        final Form layoutForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor1 = LayoutDescriptor.newLayoutDescriptor().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "left" ).build() ).
                add( newRegionDescriptor().name( "right" ).build() ).
                build() ).
            key( DescriptorKey.from( "module:fancy-layout" ) ).
            build();

        final LayoutDescriptor layoutDescriptor2 = LayoutDescriptor.newLayoutDescriptor().
            name( "putty-layout" ).
            displayName( "Putty layout" ).
            config( layoutForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "top" ).build() ).
                add( newRegionDescriptor().name( "bottom" ).build() ).
                build() ).
            key( DescriptorKey.from( "module:putty-layout" ) ).
            build();

        final LayoutDescriptors layoutDescriptors = LayoutDescriptors.from( layoutDescriptor1, layoutDescriptor2 );

        final ModuleKeys moduleKeys = ModuleKeys.from( "module1", "module2", "module3" );

        Mockito.when( layoutDescriptorService.getByModules( moduleKeys ) ).thenReturn( layoutDescriptors );

        String jsonString = request().path( "content/page/layout/descriptor/list/by_modules" ).
            entity( readFromFile( "get_by_modules_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "get_by_modules_success.json", jsonString );
    }
}