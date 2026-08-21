package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.schema.parser.YmlMixinDescriptorParser;
import com.enonic.xp.core.impl.schema.YmlFormFragmentParser;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( schemaService.updateContentType( isA( UpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateContentSchemaParams schemaParams = params.getArgument( 0, UpdateContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.updateFormFragment( isA( UpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateContentSchemaParams schemaParams = params.getArgument( 0, UpdateContentSchemaParams.class );

            final FormFragmentDescriptor.Builder builder = YmlFormFragmentParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( FormFragmentName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateFormFragment.js" );
    }

    @Test
    void testMixin()
    {
        when( schemaService.updateMixin( isA( UpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateContentSchemaParams schemaParams = params.getArgument( 0, UpdateContentSchemaParams.class );

            final MixinDescriptor.Builder builder = YmlMixinDescriptorParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( MixinName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateMixin.js" );
    }

    @Test
    void updateWithForm()
    {
        when( schemaService.updateContentType( isA( UpdateContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateContentSchemaParams schemaParams = params.getArgument( 0, UpdateContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runFunction( "/test/UpdateContentSchemaHandlerTest.js", "updateWithForm" );
    }

    @Test
    void testInvalidContentSchema()
    {
        runFunction( "/test/UpdateContentSchemaHandlerTest.js", "updateInvalidContentSchema" );
    }
}