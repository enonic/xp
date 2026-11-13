package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( dynamicSchemaService.deleteContentType( isA( ContentTypeName.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.deleteFormFragment( isA( FormFragmentName.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteFormFragment.js" );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.deleteMixin( isA( MixinName.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMixin.js" );
    }

    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicContentSchemaHandlerTest.js", "deleteInvalidContentSchemaType" );
    }
}
