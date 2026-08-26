package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteCmsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCms()
    {
        when( dynamicSchemaService.deleteCms( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteCms.js" );
    }

    @Test
    void testNullApplication()
    {
        runFunction( "/test/DeleteCmsHandlerTest.js", "deleteNull" );
    }
}
