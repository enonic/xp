package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.core.impl.content.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.content.parser.YmlMixinDescriptorParser;
import com.enonic.xp.core.impl.schema.YmlFormFragmentParser;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrUpdateContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    private static final Instant MODIFIED_TIME = Instant.parse( "2021-09-25T10:00:00.00Z" );

    @Test
    void testContentType()
    {
        when( dynamicSchemaService.createOrUpdateContentType( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateContentSchemaParams schemaParams = params.getArgument( 0, CreateOrUpdateContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );
            builder.modifiedTime( MODIFIED_TIME );
            builder.createdTime( MODIFIED_TIME );
            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
            builder.icon( Icon.from( new byte[]{1, 2, 3, 4, 5, 6}, "image/png", ts ) );
            builder.creator( PrincipalKey.ofAnonymous() );

            return new DynamicSchemaResult<>( builder.build(), resource( schemaParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.createOrUpdateFormFragment( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateContentSchemaParams schemaParams = params.getArgument( 0, CreateOrUpdateContentSchemaParams.class );

            final FormFragmentDescriptor.Builder builder =
                YmlFormFragmentParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );
            builder.modifiedTime( MODIFIED_TIME );
            builder.createdTime( MODIFIED_TIME );
            builder.name( FormFragmentName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            return new DynamicSchemaResult<>( builder.build(), resource( schemaParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateFormFragment.js" );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.createOrUpdateMixin( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateContentSchemaParams schemaParams = params.getArgument( 0, CreateOrUpdateContentSchemaParams.class );

            final MixinDescriptor.Builder builder =
                YmlMixinDescriptorParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );
            builder.modifiedTime( MODIFIED_TIME );
            builder.createdTime( MODIFIED_TIME );
            builder.name( MixinName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            return new DynamicSchemaResult<>( builder.build(), resource( schemaParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateMixin.js" );
    }

    // `{data, mimeType}` without a resource: the stored schema is kept, the icon alone is replaced
    @Test
    void testContentTypeIcon()
        throws Exception
    {
        when( dynamicSchemaService.createOrUpdateContentType( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer(
            params -> contentTypeResult( params.getArgument( 0, CreateOrUpdateContentSchemaParams.class ) ) );

        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createOrUpdateContentTypeIcon" );

        final ArgumentCaptor<CreateOrUpdateContentSchemaParams> captor = ArgumentCaptor.forClass( CreateOrUpdateContentSchemaParams.class );
        verify( dynamicSchemaService ).createOrUpdateContentType( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue().getName() );
        assertNull( captor.getValue().getResource() );

        final IconUpdate.Replace replace = (IconUpdate.Replace) captor.getValue().getIcon();
        assertEquals( "image/svg+xml", replace.mimeType() );
        assertArrayEquals( "<svg/>".getBytes(), replace.data().read() );
    }

    @Test
    void testMixinRemovingIcon()
    {
        when( dynamicSchemaService.createOrUpdateMixin( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateContentSchemaParams schemaParams = params.getArgument( 0, CreateOrUpdateContentSchemaParams.class );
            final MixinDescriptor.Builder builder = MixinDescriptor.create().name( (MixinName) schemaParams.getName() );
            builder.modifiedTime( MODIFIED_TIME ).createdTime( MODIFIED_TIME );
            return new DynamicSchemaResult<>( builder.build(), resource( "kind: \"Mixin\"" ) );
        } );

        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createOrUpdateMixinRemovingIcon" );

        final ArgumentCaptor<CreateOrUpdateContentSchemaParams> captor = ArgumentCaptor.forClass( CreateOrUpdateContentSchemaParams.class );
        verify( dynamicSchemaService ).createOrUpdateMixin( captor.capture() );
        assertEquals( IconUpdate.REMOVE, captor.getValue().getIcon() );
        assertNull( captor.getValue().getResource() );
    }

    @Test
    void testInvalidContentSchemaType()
    {
        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createInvalidContentSchemaType" );
    }

    @Test
    void testInvalidContentSchema()
    {
        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createInvalidContentSchema" );
    }

    @Test
    void testWithoutName()
    {
        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createContentSchemaWithoutName" );
    }

    @Test
    void testInvalidName()
    {
        runFunction( "/test/CreateOrUpdateContentSchemaHandlerTest.js", "createContentSchemaWithInvalidName" );
    }

    private static DynamicSchemaResult<ContentType> contentTypeResult( final CreateOrUpdateContentSchemaParams params )
    {
        final ContentType.Builder builder = ContentType.create()
            .name( (ContentTypeName) params.getName() )
            .superType( ContentTypeName.structured() )
            .modifiedTime( MODIFIED_TIME )
            .createdTime( MODIFIED_TIME );
        return new DynamicSchemaResult<>( builder.build(), resource( "kind: \"ContentType\"" ) );
    }

    private static Resource resource( final String value )
    {
        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( value );
        return resource;
    }
}