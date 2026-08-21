package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.Mockito.when;

class DeleteContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( schemaService.deleteContentType( ContentTypeName.from( "myapp:mytype" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteContentType.js" );

        Mockito.verify( schemaService ).deleteContentType( ContentTypeName.from( "myapp:mytype" ) );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.deleteFormFragment( FormFragmentName.from( "myapp:myFragment" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteFormFragment.js" );

        Mockito.verify( schemaService ).deleteFormFragment( FormFragmentName.from( "myapp:myFragment" ) );
    }

    @Test
    void testMixin()
    {
        when( schemaService.deleteMixin( MixinName.from( "myapp:mydata" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMixin.js" );

        Mockito.verify( schemaService ).deleteMixin( MixinName.from( "myapp:mydata" ) );
    }

}