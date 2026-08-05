package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicMacroParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( schemaService.updateMacro( isA( UpdateDynamicMacroParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicMacroParams macroParams = params.getArgument( 0, UpdateDynamicMacroParams.class );

            final MacroDescriptor.Builder builder =
                YmlMacroDescriptorParser.parse( macroParams.getResource(), macroParams.getKey().getApplicationKey() );
            builder.key( macroParams.getKey() );
            builder.modifiedTime( Instant.parse( "2021-09-25T10:00:00.00Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( macroParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateMacro.js" );
    }

    @Test
    void testInvalidMacro()
    {
        runFunction( "/test/UpdateDynamicMacroHandlerTest.js", "updateInvalidMacro" );
    }
}
