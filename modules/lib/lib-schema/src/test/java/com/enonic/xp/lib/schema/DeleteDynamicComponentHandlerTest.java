package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteDynamicComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testPart()
    {
        when( dynamicSchemaService.deletePart( isA( DescriptorKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePart.js" );
    }

    @Test
    void testLayout()
    {
        when( dynamicSchemaService.deleteLayout( isA( DescriptorKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteLayout.js" );
    }

    @Test
    void testPage()
    {
        when( dynamicSchemaService.deletePage( isA( DescriptorKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePage.js" );
    }

    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicComponentHandlerTest.js", "deleteInvalidComponentType" );
    }
}
