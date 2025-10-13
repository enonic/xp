package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.content.parser.YmlXDataParser;
import com.enonic.xp.core.impl.form.mixin.YmlFormFragmentParser;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.FormFragmentDescriptor;
import com.enonic.xp.schema.mixin.FormFragmentName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testContentType()
    {
        when( dynamicSchemaService.updateContentSchema( isA( UpdateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicContentSchemaParams schemaParams = params.getArgument( 0, UpdateDynamicContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateContentType.js" );
    }

    @Test
    public void testMixin()
    {
        when( dynamicSchemaService.updateContentSchema( isA( UpdateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicContentSchemaParams schemaParams = params.getArgument( 0, UpdateDynamicContentSchemaParams.class );

            final FormFragmentDescriptor.Builder builder = YmlFormFragmentParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( FormFragmentName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateMixin.js" );
    }

    @Test
    public void testXData()
    {
        when( dynamicSchemaService.updateContentSchema( isA( UpdateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicContentSchemaParams schemaParams = params.getArgument( 0, UpdateDynamicContentSchemaParams.class );

            final XData.Builder builder = YmlXDataParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( XDataName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateXData.js" );
    }

    @Test
    public void updateWithForm()
    {
        when( dynamicSchemaService.updateContentSchema( isA( UpdateDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicContentSchemaParams schemaParams = params.getArgument( 0, UpdateDynamicContentSchemaParams.class );

            final ContentType.Builder builder =
                YmlContentTypeParser.parse( schemaParams.getResource(), schemaParams.getName().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );
            builder.createdTime( modifiedTime );

            builder.name( ContentTypeName.from( schemaParams.getName().getApplicationKey(), schemaParams.getName().getLocalName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( schemaParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runFunction( "/test/UpdateDynamicContentSchemaHandlerTest.js", "updateWithForm" );
    }

    @Test
    public void testInvalidContentSchemaType()
    {
        runFunction( "/test/UpdateDynamicContentSchemaHandlerTest.js", "updateInvalidContentSchemaType" );
    }

    @Test
    public void testInvalidContentSchema()
    {
        runFunction( "/test/UpdateDynamicContentSchemaHandlerTest.js", "updateInvalidContentSchema" );
    }
}
