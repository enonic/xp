package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListDynamicSchemasHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void listContentTypes()
    {
        when( schemaService.listContentSchemas( isA( ListDynamicContentSchemasParams.class ) ) ).thenAnswer( params -> {
            final ListDynamicContentSchemasParams schemasParams = params.getArgument( 0, ListDynamicContentSchemasParams.class );

            final ContentType contentType1 = ContentType.create()
                .superType( ContentTypeName.structured() )
                .description( "My type description" )
                .title( "My type display name" )
                .name( ContentTypeName.from( schemasParams.getKey() + ":type1" ) )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .addFormItem( FieldSet.create()
                                  .label( "My layout" )
                                  .addFormItem( FormItemSet.create()
                                                    .name( "mySet" )
                                                    .required( true )
                                                    .addFormItem( Input.create()
                                                                      .name( "myInput" )
                                                                      .label( "Input" )
                                                                      .inputType( InputTypeName.TEXT_LINE )
                                                                      .build() )
                                                    .build() )
                                  .build() )
                .build();

            final ContentType contentType2 = ContentType.create()
                .superType( ContentTypeName.archiveMedia() )
                .description( "My type description 2" )
                .title( "My type display name 2" )
                .name( ContentTypeName.from( schemasParams.getKey() + ":type2" ) )
                .modifiedTime( Instant.parse( "2012-01-01T10:00:00Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "ContentType"
                superType: "base:structured"
                title: "My type display name"
                description: "My type description"
                form:
                - type: "FieldSet"
                  label: "My layout"
                  items:
                  - type: "ItemSet"
                    name: "mySet"
                    occurrences:
                      min: 1
                      max: 1
                    items:
                    - type: "TextLine"
                      name: "myInput"
                      label: "Input"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "ContentType"
                superType: "media:archive"
                title: "My type display name 2"
                description: "My type description 2"
                """ );

            return List.of( new DynamicSchemaResult<ContentType>( contentType1, resource1 ),
                            new DynamicSchemaResult<ContentType>( contentType2, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listContentTypes.js" );
    }

    @Test
    void listFormFragments()
    {
        when( schemaService.listContentSchemas( isA( ListDynamicContentSchemasParams.class ) ) ).thenAnswer( params -> {
            final ListDynamicContentSchemasParams schemasParams = params.getArgument( 0, ListDynamicContentSchemasParams.class );

            final FormFragmentDescriptor fragment1 = FormFragmentDescriptor.create()
                .name( FormFragmentName.from( schemasParams.getKey() + ":fragment1" ) )
                .description( "My FormFragment description" )
                .title( "My FormFragment display name" )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .addFormItem(
                    Input.create().name( "inputToBeMixedIn" ).label( "Mixed in" ).inputType( InputTypeName.TEXT_LINE ).build() )
                .build();

            final FormFragmentDescriptor fragment2 = FormFragmentDescriptor.create()
                .name( FormFragmentName.from( schemasParams.getKey() + ":fragment2" ) )
                .title( "Other fragment" )
                .modifiedTime( Instant.parse( "2012-01-01T10:00:00Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "FormFragment"
                title: "My FormFragment display name"
                description: "My FormFragment description"
                form:
                - type: "TextLine"
                  name: "inputToBeMixedIn"
                  label: "Mixed in"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "FormFragment"
                title: "Other fragment"
                """ );

            return List.of( new DynamicSchemaResult<FormFragmentDescriptor>( fragment1, resource1 ),
                            new DynamicSchemaResult<FormFragmentDescriptor>( fragment2, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listFormFragments.js" );
    }

    @Test
    void listMixins()
    {
        when( schemaService.listContentSchemas( isA( ListDynamicContentSchemasParams.class ) ) ).thenAnswer( params -> {
            final ListDynamicContentSchemasParams schemasParams = params.getArgument( 0, ListDynamicContentSchemasParams.class );

            final MixinDescriptor mixin1 = MixinDescriptor.create()
                .name( MixinName.from( schemasParams.getKey() + ":mixin1" ) )
                .title( "My mixin display name" )
                .description( "My mixin description" )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .build();

            final MixinDescriptor mixin2 = MixinDescriptor.create()
                .name( MixinName.from( schemasParams.getKey() + ":mixin2" ) )
                .title( "Other mixin" )
                .modifiedTime( Instant.parse( "2012-01-01T10:00:00Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( """
                kind: "Mixin"
                title: "My mixin display name"
                description: "My mixin description"
                """ );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( """
                kind: "Mixin"
                title: "Other mixin"
                """ );

            return List.of( new DynamicSchemaResult<MixinDescriptor>( mixin1, resource1 ),
                            new DynamicSchemaResult<MixinDescriptor>( mixin2, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listMixins.js" );
    }
}
