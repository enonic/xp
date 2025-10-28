package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateDynamicComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testPart()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final XmlPartDescriptorParser parser = new XmlPartDescriptorParser();

            PartDescriptor.Builder builder = PartDescriptor.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            parser.builder( builder );
            parser.source( componentParams.getResource() );
            parser.currentApplication( componentParams.getKey().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<PartDescriptor>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createPart.js" );
    }

    @Test
    void testLayout()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();

            LayoutDescriptor.Builder builder = LayoutDescriptor.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            parser.builder( builder );
            parser.source( componentParams.getResource() );
            parser.currentApplication( componentParams.getKey().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<LayoutDescriptor>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createLayout.js" );
    }

    @Test
    void testPage()
    {
        when( dynamicSchemaService.createComponent( isA( CreateDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicComponentParams componentParams = params.getArgument( 0, CreateDynamicComponentParams.class );

            final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();

            PageDescriptor.Builder builder = PageDescriptor.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            builder.key( DescriptorKey.from( componentParams.getKey().getApplicationKey(), componentParams.getKey().getName() ) );

            parser.builder( builder );
            parser.source( componentParams.getResource() );
            parser.currentApplication( componentParams.getKey().getApplicationKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( componentParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createPage.js" );
    }


    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/CreateDynamicComponentHandlerTest.js", "createInvalidComponentType" );
    }

    @Test
    void testInvalidSchema()
    {
        runFunction( "/test/CreateDynamicComponentHandlerTest.js", "createInvalidComponent" );
    }
}
