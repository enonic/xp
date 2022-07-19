package com.enonic.xp.lib.schema;


import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class DeleteDynamicStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testStyles()
    {
        when( dynamicSchemaService.deleteStyles( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteStyles.js" );
    }


    @Test
    public void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicStylesHandlerTest.js", "deleteNull" );
    }
}
