package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.parser.YmlStyleDescriptorParser;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( schemaService.createStyles( isA( CreateStylesParams.class ) ) ).thenAnswer( params -> {
            final CreateStylesParams stylesParams = params.getArgument( 0, CreateStylesParams.class );

            final StyleDescriptor.Builder builder = YmlStyleDescriptorParser.parse( stylesParams.getResource(), stylesParams.getKey() );

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( stylesParams.getResource() );

            return new SchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createStyles.js" );
    }

    @Test
    void testInvalidStyles()
    {
        runFunction( "/test/CreateStylesHandlerTest.js", "createInvalidStyles" );
    }
}
