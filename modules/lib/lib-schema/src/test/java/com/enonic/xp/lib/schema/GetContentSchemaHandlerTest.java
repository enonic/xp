package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( schemaService.getContentType( isA( ContentTypeName.class ) ) ).thenAnswer( params -> {
            final ContentTypeName contentTypeName = params.getArgument( 0, ContentTypeName.class );

            final ContentType contentType = ContentType.create()
                .superType( ContentTypeName.structured() )
                .description( "My type description" )
                .title( "My type display name" )
                .name( contentTypeName )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .allowChildContentType( List.of( "myapp:other-type", "myapp:another-type" ) )
                .displayNamePlaceholder( "Enter a display name" )
                .displayNameExpression( "${title}" )
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

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( """
                kind: "ContentType"
                superType: "base:structured"
                title: "My type display name"
                description: "My type description"
                allowChildContentType:
                - "myapp:other-type"
                - "myapp:another-type"
                displayNamePlaceholder: "Enter a display name"
                displayNameExpression: "${title}"
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

            return new SchemaResult<ContentType>( contentType, resource );
        } );

        runScript( "/lib/xp/examples/schema/getContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.getFormFragment( isA( FormFragmentName.class ) ) ).thenAnswer( params -> {
            final FormFragmentName fragmentName = params.getArgument( 0, FormFragmentName.class );

            final FormFragmentDescriptor fragmentDescriptor = FormFragmentDescriptor.create()
                .name( fragmentName )
                .description( "My FormFragment description" )
                .title( "My FormFragment display name" )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .createdTime( Instant.parse( "2009-01-01T10:00:00Z" ) )
                .creator( PrincipalKey.ofAnonymous() )
                .addFormItem( Input.create().name( "inputToBeMixedIn" ).label( "Mixed in" ).inputType( InputTypeName.TEXT_LINE ).build() )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( """
                kind: "FormFragment"
                title: "My FormFragment display name"
                description: "My FormFragment description"
                form:
                - type: "TextLine"
                  name: "inputToBeMixedIn"
                  label: "Mixed in"
                """ );

            return new SchemaResult<>( fragmentDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getFormFragment.js" );
    }

    @Test
    void testMixinDescriptor()
    {
        when( schemaService.getMixin( isA( MixinName.class ) ) ).thenAnswer( params -> {
            final MixinDescriptor mixinDescriptor = MixinDescriptor.create()
                .name( CAMERA_INFO_METADATA_NAME )
                .title( "Photo Info" )
                .titleI18nKey( "media.cameraInfo.displayName" )
                .modifiedTime( Instant.ofEpochMilli( 443234242L ) )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( """
                kind: "Mixin"
                title:
                  text: "Photo Info"
                  i18n: "media.cameraInfo.displayName"
                """ );

            return new SchemaResult<>( mixinDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getMixin.js" );
    }

    @Test
    void testNull()
    {
        when( schemaService.getFormFragment( isA( FormFragmentName.class ) ) ).thenReturn( null );
        runFunction( "/test/GetContentSchemaHandlerTest.js", "getNullSchema" );
    }

}