package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.security.User;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testContentType()
    {
        when( dynamicSchemaService.getContentSchema( isA( GetDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicContentSchemaParams schemaParams = params.getArgument( 0, GetDynamicContentSchemaParams.class );

            if ( DynamicContentSchemaType.CONTENT_TYPE != schemaParams.getType() )
            {
                throw new IllegalArgumentException( "invalid content schema type: " + schemaParams.getType() );
            }

            final ContentType contentType = ContentType.create()
                .superType( ContentTypeName.structured() )
                .description( "My type description" )
                .displayName( "My type display name" )
                .name( (ContentTypeName) schemaParams.getName() )
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

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<content-type><some-data></some-data></content-type>" );

            return new DynamicSchemaResult<ContentType>( contentType, resource );
        } );

        runScript( "/lib/xp/examples/schema/getContentType.js" );
    }

    @Test
    public void testMixin()
    {
        when( dynamicSchemaService.getContentSchema( isA( GetDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicContentSchemaParams schemaParams = params.getArgument( 0, GetDynamicContentSchemaParams.class );

            if ( DynamicContentSchemaType.FORM_FRAGMENT != schemaParams.getType() )
            {
                throw new IllegalArgumentException( "invalid content schema type: " + schemaParams.getType() );
            }

            final FormFragmentDescriptor mixin = FormFragmentDescriptor.create()
                .name( (FormFragmentName) schemaParams.getName() )
                .description( "My mixin description" )
                .displayName( "My mixin display name" )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .createdTime( Instant.parse( "2009-01-01T10:00:00Z" ) )
                .creator( User.ANONYMOUS.getKey() )
                .addFormItem( Input.create().name( "inputToBeMixedIn" ).label( "Mixed in" ).inputType( InputTypeName.TEXT_LINE ).build() )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( """
                                                          displayName: "Virtual FormFragment"
                                                          description: "FormFragment description"
                                                          form:
                                                          - type: "TextLine"
                                                            name: "text"
                                                            label: "Text"
                                                          """ );

            return new DynamicSchemaResult<>( mixin, resource );
        } );

        runScript( "/lib/xp/examples/schema/getMixin.js" );
    }

    @Test
    public void testXData()
    {
        when( dynamicSchemaService.getContentSchema( isA( GetDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final GetDynamicContentSchemaParams schemaParams = params.getArgument( 0, GetDynamicContentSchemaParams.class );

            if ( DynamicContentSchemaType.XDATA != schemaParams.getType() )
            {
                throw new IllegalArgumentException( "invalid content schema type: " + schemaParams.getType() );
            }

            final XData xData = XData.create()
                .name( CAMERA_INFO_METADATA_NAME )
                .displayName( "Photo Info" )
                .displayNameI18nKey( "media.cameraInfo.displayName" )
                .modifiedTime( Instant.ofEpochMilli( 443234242L ) )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<x-data><some-data></some-data></x-data>" );

            return new DynamicSchemaResult<XData>( xData, resource );
        } );

        runScript( "/lib/xp/examples/schema/getXData.js" );
    }


    @Test
    public void testInvalidSchemaType()
    {
        runFunction( "/test/GetDynamicContentSchemaHandlerTest.js", "getInvalidContentSchemaType" );
    }

    @Test
    public void testNull()
    {
        when( dynamicSchemaService.getContentSchema( isA( GetDynamicContentSchemaParams.class ) ) ).thenReturn( null );
        runFunction( "/test/GetDynamicContentSchemaHandlerTest.js", "getNullSchema" );
    }

}
