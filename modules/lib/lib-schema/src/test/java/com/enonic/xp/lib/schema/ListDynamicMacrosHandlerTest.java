package com.enonic.xp.lib.schema;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class ListDynamicMacrosHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacros()
    {
        when( dynamicSchemaService.listMacros( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );
            return List.of( GetDynamicMacroHandlerTest.macroResult( applicationKey + ":mymacro" ) );
        } );

        runScript( "/lib/xp/examples/schema/listMacros.js" );
    }
}
