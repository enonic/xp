package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlPageDescriptorParser;
import com.enonic.xp.core.impl.schema.parser.YmlPartDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.UpdateComponentParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testPart()
    {
        when( schemaService.updatePart( isA( UpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final UpdateComponentParams componentParams = params.getArgument( 0, UpdateComponentParams.class );

            final PartDescriptor.Builder builder =
                YmlPartDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updatePart.js" );
    }

    @Test
    void testLayout()
    {
        when( schemaService.updateLayout( isA( UpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final UpdateComponentParams componentParams = params.getArgument( 0, UpdateComponentParams.class );

            final LayoutDescriptor.Builder builder =
                YmlLayoutDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateLayout.js" );
    }

    @Test
    void testPage()
    {
        when( schemaService.updatePage( isA( UpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final UpdateComponentParams componentParams = params.getArgument( 0, UpdateComponentParams.class );

            final DescriptorKey descriptorKey =
                DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() );

            final PageDescriptor.Builder builder =
                YmlPageDescriptorParser.parse( componentParams.getResource(), descriptorKey.getApplicationKey() )
                    .key( descriptorKey )
                    .modifiedTime( Instant.parse( "2021-09-25T10:00:00.00Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updatePage.js" );
    }

    @Test
    void testInvalidSchema()
    {
        runFunction( "/test/UpdateComponentHandlerTest.js", "updateInvalidComponent" );
    }
}
