package com.enonic.xp.lib.schema;


import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( schemaService.deleteStyles( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteStyles.js" );
    }


    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteStylesHandlerTest.js", "deleteNull" );
    }
}
