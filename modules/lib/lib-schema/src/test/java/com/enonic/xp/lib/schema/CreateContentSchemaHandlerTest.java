package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.schema.parser.YmlMixinDescriptorParser;
import com.enonic.xp.core.impl.schema.YmlFormFragmentParser;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( schemaService.createContentType( isA( CreateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateContentSchemaParams schemaParams = params.getArgument( 0, CreateContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
            final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
            Icon icon = Icon.from( data, "image/png", ts );

            builder.icon( icon );
            builder.creator( PrincipalKey.ofAnonymous() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.createFormFragment( isA( CreateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateContentSchemaParams schemaParams = params.getArgument( 0, CreateContentSchemaParams.class );

            final FormFragmentDescriptor.Builder builder = YmlFormFragmentParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( FormFragmentName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createFormFragment.js" );
    }

    @Test
    void testMixin()
    {
        when( schemaService.createMixin( isA( CreateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final CreateContentSchemaParams schemaParams = params.getArgument( 0, CreateContentSchemaParams.class );

            final MixinDescriptor.Builder builder = YmlMixinDescriptorParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( MixinName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createMixin.js" );
    }

    @Test
    void testInvalidContentSchema()
    {
        runFunction( "/test/CreateContentSchemaHandlerTest.js", "createInvalidContentSchema" );
    }

}