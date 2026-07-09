package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.DeleteMacroParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( schemaService.deleteMacro( isA( DeleteMacroParams.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacro.js" );
    }
}
