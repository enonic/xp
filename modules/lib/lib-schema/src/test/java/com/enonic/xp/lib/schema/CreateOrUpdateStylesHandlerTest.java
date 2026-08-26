package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlStyleDescriptorParser;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateOrUpdateStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( dynamicSchemaService.createOrUpdateStyles( isA( CreateOrUpdateStylesParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateStylesParams stylesParams = params.getArgument( 0, CreateOrUpdateStylesParams.class );

            final StyleDescriptor.Builder builder = YmlStyleDescriptorParser.parse( stylesParams.getResource(), stylesParams.getKey() );
            builder.modifiedTime( Instant.parse( "2021-09-25T10:00:00.00Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( stylesParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateStyles.js" );
    }

    @Test
    void testInvalidStyles()
    {
        runFunction( "/test/CreateOrUpdateStylesHandlerTest.js", "createInvalidStyles" );
    }

    @Test
    void testWithoutResource()
    {
        runFunction( "/test/CreateOrUpdateStylesHandlerTest.js", "createStylesWithoutResource" );
    }
}