package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DeleteDynamicMacroParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( schemaService.deleteMacro( isA( DeleteDynamicMacroParams.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacro.js" );
    }
}
