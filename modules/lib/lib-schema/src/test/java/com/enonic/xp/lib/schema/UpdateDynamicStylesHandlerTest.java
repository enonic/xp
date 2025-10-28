package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.parser.YmlStyleDescriptorParser;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateDynamicStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( dynamicSchemaService.updateStyles( isA( UpdateDynamicStylesParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicStylesParams stylesParams = params.getArgument( 0, UpdateDynamicStylesParams.class );

            final StyleDescriptor.Builder builder = YmlStyleDescriptorParser.parse( stylesParams.getResource(), stylesParams.getKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( stylesParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateStyles.js" );
    }

    @Test
    void testInvalidStyles()
    {
        runFunction( "/test/CreateDynamicStylesHandlerTest.js", "createInvalidStyles" );
    }
}
