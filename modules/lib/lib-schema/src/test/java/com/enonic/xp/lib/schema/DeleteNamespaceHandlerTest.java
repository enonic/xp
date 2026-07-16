package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteNamespaceHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testDeleteNamespace()
    {
        when( applicationService.deleteNamespace( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteNamespace.js" );
    }
}
