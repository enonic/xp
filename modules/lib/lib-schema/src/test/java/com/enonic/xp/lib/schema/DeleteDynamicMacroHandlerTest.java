package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.macro.MacroKey;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( dynamicSchemaService.deleteMacro( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacro.js" );

        verify( dynamicSchemaService ).deleteMacro( MacroKey.from( "myapp:mymacro" ) );
    }
}
