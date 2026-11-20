package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.core.impl.content.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPageDescriptorParser;
import com.enonic.xp.core.impl.content.parser.YmlPartDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrUpdateComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    private static final Instant MODIFIED_TIME = Instant.parse( "2021-09-25T10:00:00.00Z" );

    @Test
    void testPart()
    {
        when( dynamicSchemaService.createOrUpdatePart( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateComponentParams componentParams = params.getArgument( 0, CreateOrUpdateComponentParams.class );

            final PartDescriptor.Builder builder =
                YmlPartDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );
            builder.modifiedTime( MODIFIED_TIME );
            builder.key( componentParams.getKey() );
            builder.icon( icon( componentParams.getIcon() ) );

            return new DynamicSchemaResult<>( builder.build(), resource( componentParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdatePart.js" );

        final ArgumentCaptor<CreateOrUpdateComponentParams> captor = ArgumentCaptor.forClass( CreateOrUpdateComponentParams.class );
        verify( dynamicSchemaService ).createOrUpdatePart( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue().getKey() );
        assertEquals( "image/svg+xml", ( (IconUpdate.Replace) captor.getValue().getIcon() ).mimeType() );
    }

    @Test
    void testLayout()
    {
        when( dynamicSchemaService.createOrUpdateLayout( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateComponentParams componentParams = params.getArgument( 0, CreateOrUpdateComponentParams.class );

            final LayoutDescriptor.Builder builder =
                YmlLayoutDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() );
            builder.modifiedTime( MODIFIED_TIME );
            builder.key( componentParams.getKey() );

            return new DynamicSchemaResult<>( builder.build(), resource( componentParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateLayout.js" );
    }

    @Test
    void testPage()
    {
        when( dynamicSchemaService.createOrUpdatePage( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateComponentParams componentParams = params.getArgument( 0, CreateOrUpdateComponentParams.class );

            final PageDescriptor.Builder builder =
                YmlPageDescriptorParser.parse( componentParams.getResource(), componentParams.getKey().getApplicationKey() )
                    .key( componentParams.getKey() )
                    .modifiedTime( MODIFIED_TIME );

            return new DynamicSchemaResult<>( builder.build(), resource( componentParams.getResource() ) );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdatePage.js" );
    }

    // the icon is left out: the stored one is kept, the resource alone is updated
    @Test
    void testPartKeepingIcon()
    {
        when( dynamicSchemaService.createOrUpdatePart( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer(
            params -> partResult( params.getArgument( 0, CreateOrUpdateComponentParams.class ) ) );

        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createOrUpdatePartKeepingIcon" );

        final ArgumentCaptor<CreateOrUpdateComponentParams> captor = ArgumentCaptor.forClass( CreateOrUpdateComponentParams.class );
        verify( dynamicSchemaService ).createOrUpdatePart( captor.capture() );
        assertEquals( IconUpdate.KEEP, captor.getValue().getIcon() );
        assertEquals( "kind: \"Part\"", captor.getValue().getResource() );
    }

    // `icon: null` without a resource: the stored descriptor is kept, the icon alone is removed
    @Test
    void testPartRemovingIcon()
    {
        when( dynamicSchemaService.createOrUpdatePart( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer(
            params -> partResult( params.getArgument( 0, CreateOrUpdateComponentParams.class ) ) );

        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createOrUpdatePartRemovingIcon" );

        final ArgumentCaptor<CreateOrUpdateComponentParams> captor = ArgumentCaptor.forClass( CreateOrUpdateComponentParams.class );
        verify( dynamicSchemaService ).createOrUpdatePart( captor.capture() );
        assertEquals( IconUpdate.REMOVE, captor.getValue().getIcon() );
        assertNull( captor.getValue().getResource() );
    }

    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createInvalidComponentType" );
    }

    @Test
    void testInvalidSchema()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createInvalidComponent" );
    }

    @Test
    void testWithoutKey()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createComponentWithoutKey" );
    }

    @Test
    void testWithoutResource()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createComponentWithoutResource" );
    }

    @Test
    void testInvalidKey()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createComponentWithInvalidKey" );
    }

    @Test
    void testIncompleteIcon()
    {
        runFunction( "/test/CreateOrUpdateComponentHandlerTest.js", "createPartWithIncompleteIcon" );
    }

    private static DynamicSchemaResult<PartDescriptor> partResult( final CreateOrUpdateComponentParams params )
    {
        final String resource = params.getResource() != null ? params.getResource() : "kind: \"Part\"";
        final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( resource, params.getKey().getApplicationKey() );
        builder.key( params.getKey() ).modifiedTime( MODIFIED_TIME );
        return new DynamicSchemaResult<>( builder.build(), resource( resource ) );
    }

    private static Icon icon( final IconUpdate iconUpdate )
    {
        if ( !( iconUpdate instanceof IconUpdate.Replace replace ) )
        {
            return null;
        }
        try
        {
            return Icon.from( replace.data().read(), replace.mimeType(), MODIFIED_TIME );
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static Resource resource( final String value )
    {
        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( value );
        return resource;
    }
}