package com.enonic.xp.admin.impl.rest.resource.content.page.layout;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

public class LayoutDescriptorResourceTest
    extends AdminResourceTestSupport
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
        final DescriptorKey key = DescriptorKey.from( "application:fancy-layout" );
        final Form layoutForm = Form.create().
            addFormItem( Input.create().
                name( "columns" ).
                maximizeUIInputWidth( true ).
                label( "Columns" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
            key( key ).
            build();

        Mockito.when( layoutDescriptorService.getByKey( key ) ).thenReturn( layoutDescriptor );

        String jsonString = request().path( "content/page/layout/descriptor" ).
            queryParam( "key", "application:fancy-layout" ).get().getAsString();

        assertJson( "get_by_key_success.json", jsonString );
    }

    @Test
    public void test_get_by_applications()
        throws Exception
    {
        final Form layoutForm = Form.create().
            addFormItem( Input.create().name( "columns" ).label( "Columns" ).inputType( InputTypeName.DOUBLE ).build() ).
            build();

        final LayoutDescriptor layoutDescriptor1 = LayoutDescriptor.create().
            name( "fancy-layout" ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:fancy-layout" ) ).
            build();

        final LayoutDescriptor layoutDescriptor2 = LayoutDescriptor.create().
            name( "putty-layout" ).
            displayName( "Putty layout" ).
            config( layoutForm ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "top" ).build() ).
                add( RegionDescriptor.create().name( "bottom" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:putty-layout" ) ).
            build();

        final LayoutDescriptors layoutDescriptors = LayoutDescriptors.from( layoutDescriptor1, layoutDescriptor2 );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( "application1", "application2", "application3" );

        Mockito.when( layoutDescriptorService.getByApplications( applicationKeys ) ).thenReturn( layoutDescriptors );

        String jsonString = request().path( "content/page/layout/descriptor/list/by_applications" ).
            entity( readFromFile( "get_by_applications_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "get_by_applications_success.json", jsonString );
    }
}