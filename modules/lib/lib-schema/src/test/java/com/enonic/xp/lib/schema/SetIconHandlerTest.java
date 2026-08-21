package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.SetComponentIconParams;
import com.enonic.xp.schema.SetMacroIconParams;
import com.enonic.xp.schema.SetSchemaIconParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SetIconHandlerTest
    extends BaseSchemaHandlerTest
{
    private static final Icon ICON =
        Icon.from( "<svg/>".getBytes( StandardCharsets.UTF_8 ), "image/svg+xml", Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );

    @Test
    void testContentType()
        throws Exception
    {
        when( schemaService.setContentTypeIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setContentTypeIcon.js" );

        final ArgumentCaptor<SetSchemaIconParams> captor = ArgumentCaptor.forClass( SetSchemaIconParams.class );
        Mockito.verify( schemaService ).setContentTypeIcon( captor.capture() );

        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue().getName() );
        assertEquals( "image/svg+xml", captor.getValue().getMimeType() );
        assertArrayEquals( "<svg/>".getBytes( StandardCharsets.UTF_8 ), captor.getValue().getData().read() );
    }

    @Test
    void testFormFragment()
    {
        when( schemaService.setFormFragmentIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setFormFragmentIcon.js" );

        final ArgumentCaptor<SetSchemaIconParams> captor = ArgumentCaptor.forClass( SetSchemaIconParams.class );
        Mockito.verify( schemaService ).setFormFragmentIcon( captor.capture() );

        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue().getName() );
    }

    @Test
    void testMixin()
    {
        when( schemaService.setMixinIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setMixinIcon.js" );

        final ArgumentCaptor<SetSchemaIconParams> captor = ArgumentCaptor.forClass( SetSchemaIconParams.class );
        Mockito.verify( schemaService ).setMixinIcon( captor.capture() );

        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue().getName() );
    }

    @Test
    void testPart()
    {
        when( schemaService.setPartIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setPartIcon.js" );

        final ArgumentCaptor<SetComponentIconParams> captor = ArgumentCaptor.forClass( SetComponentIconParams.class );
        Mockito.verify( schemaService ).setPartIcon( captor.capture() );

        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue().getKey() );
    }

    @Test
    void testMacro()
    {
        when( schemaService.setMacroIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setMacroIcon.js" );

        final ArgumentCaptor<SetMacroIconParams> captor = ArgumentCaptor.forClass( SetMacroIconParams.class );
        Mockito.verify( schemaService ).setMacroIcon( captor.capture() );

        assertEquals( MacroKey.from( "myapp:mymacro" ), captor.getValue().getKey() );
    }
}
