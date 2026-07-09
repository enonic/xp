package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListComponentsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testParts()
    {
        when( schemaService.listParts( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final Form partForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final PartDescriptor partDescriptor = PartDescriptor.create()
                .title( "News part" )
                .config( partForm )
                .key( DescriptorKey.from( applicationKey, "part1" ) )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news part" )
                .descriptionI18nKey( "key.description" )
                .build();

            final PartDescriptor otherPartDescriptor = PartDescriptor.create()
                .title( "Other part" )
                .config( Form.empty() )
                .key( DescriptorKey.from( applicationKey, "part2" ) )
                .modifiedTime( Instant.parse( "2022-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "Part"
                title: "News part"
                description:
                  text: "My news part"
                  i18n: "key.description"
                form:
                - type: "Double"
                  name: "width"
                  label: "width"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "Part"
                title: "Other part"
                """ );

            return List.of( new SchemaResult<PartDescriptor>( partDescriptor, resource1 ),
                            new SchemaResult<PartDescriptor>( otherPartDescriptor, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listParts.js" );
    }

    @Test
    void testLayouts()
    {
        when( schemaService.listLayouts( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final Form layoutForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
                .title( "News layout" )
                .config( layoutForm )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-one" ).build() ).build() )
                .key( DescriptorKey.from( applicationKey, "layout1" ) )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news layout" )
                .descriptionI18nKey( "key.description" )
                .build();

            final LayoutDescriptor otherLayoutDescriptor = LayoutDescriptor.create()
                .title( "Other layout" )
                .config( Form.empty() )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-two" ).build() ).build() )
                .key( DescriptorKey.from( applicationKey, "layout2" ) )
                .modifiedTime( Instant.parse( "2022-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "Layout"
                title: "News layout"
                description:
                  text: "My news layout"
                  i18n: "key.description"
                form:
                - type: "Double"
                  name: "width"
                  label: "width"
                regions:
                - "region-one"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "Layout"
                title: "Other layout"
                regions:
                - "region-two"
                """ );

            return List.of( new SchemaResult<LayoutDescriptor>( layoutDescriptor, resource1 ),
                            new SchemaResult<LayoutDescriptor>( otherLayoutDescriptor, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listLayouts.js" );
    }

    @Test
    void testPages()
    {
        when( schemaService.listPages( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final Form pageForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final PageDescriptor pageDescriptor = PageDescriptor.create()
                .title( "News page" )
                .config( pageForm )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-one" ).build() ).build() )
                .key( DescriptorKey.from( applicationKey, "page1" ) )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news page" )
                .descriptionI18nKey( "key.description" )
                .build();

            final PageDescriptor otherPageDescriptor = PageDescriptor.create()
                .title( "Other page" )
                .config( Form.empty() )
                .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region-two" ).build() ).build() )
                .key( DescriptorKey.from( applicationKey, "page2" ) )
                .modifiedTime( Instant.parse( "2022-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "Page"
                title: "News page"
                description:
                  text: "My news page"
                  i18n: "key.description"
                form:
                - type: "Double"
                  name: "width"
                  label: "width"
                regions:
                - "region-one"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "Page"
                title: "Other page"
                regions:
                - "region-two"
                """ );

            return List.of( new SchemaResult<PageDescriptor>( pageDescriptor, resource1 ),
                            new SchemaResult<PageDescriptor>( otherPageDescriptor, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listPages.js" );
    }
}