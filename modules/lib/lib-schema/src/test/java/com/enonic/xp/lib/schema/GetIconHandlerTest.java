package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.mockito.Mockito.when;

class GetIconHandlerTest
    extends BaseSchemaHandlerTest
{
    private static final Icon ICON =
        Icon.from( "<svg/>".getBytes( StandardCharsets.UTF_8 ), "image/svg+xml", Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );

    @Test
    void testContentType()
    {
        when( schemaService.getContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getContentTypeIcon.js" );

        Mockito.verify( schemaService ).getContentTypeIcon( ContentTypeName.from( "myapp:mytype" ) );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.getFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getFormFragmentIcon.js" );

        Mockito.verify( schemaService ).getFormFragmentIcon( FormFragmentName.from( "myapp:myfragment" ) );
    }

    @Test
    void testMixin()
    {
        when( schemaService.getMixinIcon( MixinName.from( "myapp:mymixin" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getMixinIcon.js" );

        Mockito.verify( schemaService ).getMixinIcon( MixinName.from( "myapp:mymixin" ) );
    }

    @Test
    void testPart()
    {
        when( schemaService.getPartIcon( DescriptorKey.from( "myapp:mypart" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getPartIcon.js" );

        Mockito.verify( schemaService ).getPartIcon( DescriptorKey.from( "myapp:mypart" ) );
    }

    @Test
    void testMacro()
    {
        when( schemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getMacroIcon.js" );

        Mockito.verify( schemaService ).getMacroIcon( MacroKey.from( "myapp:mymacro" ) );
    }
}
