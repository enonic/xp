package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testPart()
    {
        when( dynamicSchemaService.getComponent( isA( GetDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicComponentParams componentParams = params.getArgument( 0, GetDynamicComponentParams.class );

            if ( DynamicComponentType.PART != componentParams.getType() )
            {
                throw new IllegalArgumentException( "invalid component type: " + componentParams.getType() );
            }

            final Form partForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final PartDescriptor partDescriptor = PartDescriptor.create()
                .displayName( "News part" )
                .config( partForm )
                .key( componentParams.getKey() )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news part" )
                .descriptionI18nKey( "key.description" )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<part><some-data></some-data></part>" );

            return new DynamicSchemaResult<PartDescriptor>( partDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getPart.js" );
    }

    @Test
    void testLayout()
    {
        when( dynamicSchemaService.getComponent( isA( GetDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicComponentParams componentParams = params.getArgument( 0, GetDynamicComponentParams.class );

            if ( DynamicComponentType.LAYOUT != componentParams.getType() )
            {
                throw new IllegalArgumentException( "invalid component type: " + componentParams.getType() );
            }

            final Form layoutForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
                .displayName( "News layout" )
                .config( layoutForm )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-one" ).build() ).build() )
                .key( componentParams.getKey() )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news layout" )
                .descriptionI18nKey( "key.description" )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<layout><some-data></some-data></layout>" );

            return new DynamicSchemaResult<LayoutDescriptor>( layoutDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getLayout.js" );
    }

    @Test
    void testPage()
    {
        when( dynamicSchemaService.getComponent( isA( GetDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicComponentParams componentParams = params.getArgument( 0, GetDynamicComponentParams.class );

            if ( DynamicComponentType.PAGE != componentParams.getType() )
            {
                throw new IllegalArgumentException( "invalid component type: " + componentParams.getType() );
            }

            final Form pageForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final PageDescriptor pageDescriptor = PageDescriptor.create()
                .displayName( "News page" )
                .config( pageForm )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-one" ).build() ).build() )
                .key( componentParams.getKey() )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news page" )
                .descriptionI18nKey( "key.description" )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<page><some-data></some-data></page>" );

            return new DynamicSchemaResult<PageDescriptor>( pageDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getPage.js" );
    }


    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/GetDynamicComponentHandlerTest.js", "getInvalidComponentType" );
    }

}
