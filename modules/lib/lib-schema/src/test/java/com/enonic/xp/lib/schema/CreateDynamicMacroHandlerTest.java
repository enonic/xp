package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( dynamicSchemaService.createMacro( isA( CreateDynamicMacroParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicMacroParams macroParams = params.getArgument( 0, CreateDynamicMacroParams.class );

            final MacroDescriptor.Builder builder =
                YmlMacroDescriptorParser.parse( macroParams.getResource(), macroParams.getKey().getApplicationKey() );
            builder.key( macroParams.getKey() );
            builder.modifiedTime( Instant.parse( "2021-09-25T10:00:00.00Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( macroParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createMacro.js" );
    }

    @Test
    void testInvalidMacro()
    {
        runFunction( "/test/CreateDynamicMacroHandlerTest.js", "createInvalidMacro" );
    }
}
