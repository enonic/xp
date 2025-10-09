package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPageDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPartDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateDynamicComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testPart()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final PartDescriptor.Builder builder =
                YmlPartDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createPart.js" );
    }

    @Test
    public void testLayout()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final LayoutDescriptor.Builder builder =
                YmlLayoutDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createLayout.js" );
    }

    @Test
    public void testPage()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final DescriptorKey descriptorKey =
                DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() );

            final PageDescriptor.Builder builder =
                YmlPageDescriptorParser.parse( componentParams.getResource(), descriptorKey.getApplicationKey() )
                    .key( descriptorKey )
                    .modifiedTime( Instant.parse( "2021-09-25T10:00:00.00Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createPage.js" );
    }


    @Test
    public void testInvalidSchemaType()
    {
        runFunction( "/test/CreateDynamicComponentHandlerTest.js", "createInvalidComponentType" );
    }

    @Test
    public void testInvalidSchema()
    {
        runFunction( "/test/CreateDynamicComponentHandlerTest.js", "createInvalidComponent" );
    }
}
