package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.Mockito.when;

class DeleteIconHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( schemaService.deleteContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteContentTypeIcon.js" );

        Mockito.verify( schemaService ).deleteContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.deleteFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteFormFragmentIcon.js" );

        Mockito.verify( schemaService ).deleteFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) );
    }

    @Test
    void testMixin()
    {
        when( schemaService.deleteMixinIcon( MixinName.from( "myapp:mymixin" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMixinIcon.js" );

        Mockito.verify( schemaService ).deleteMixinIcon( MixinName.from( "myapp:mymixin" ) );
    }

    @Test
    void testPart()
    {
        when( schemaService.deletePartIcon( DescriptorKey.from( "myapp:mypart" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePartIcon.js" );

        Mockito.verify( schemaService ).deletePartIcon( DescriptorKey.from( "myapp:mypart" ) );
    }

    @Test
    void testMacro()
    {
        when( schemaService.deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacroIcon.js" );

        Mockito.verify( schemaService ).deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) );
    }
}
