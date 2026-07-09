package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.Mockito.when;

class DeleteCmsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testDeleteCms()
    {
        when( schemaService.deleteCms( ApplicationKey.from( "myapp" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteCms.js" );
    }
}
