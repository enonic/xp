package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.SetDynamicComponentIconParams;
import com.enonic.xp.resource.SetDynamicContentSchemaIconParams;
import com.enonic.xp.resource.SetDynamicMacroIconParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SetDynamicIconHandlerTest
    extends BaseSchemaHandlerTest
{
    static final Icon ICON =
        Icon.from( "<svg/>".getBytes( StandardCharsets.UTF_8 ), "image/svg+xml", Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );

    @Test
    void testContentType()
        throws Exception
    {
        when( dynamicSchemaService.setContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setContentTypeIcon.js" );

        final ArgumentCaptor<SetDynamicContentSchemaIconParams> captor = ArgumentCaptor.forClass( SetDynamicContentSchemaIconParams.class );
        verify( dynamicSchemaService ).setContentSchemaIcon( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.CONTENT_TYPE, captor.getValue().getType() );
        assertEquals( "image/svg+xml", captor.getValue().getMimeType() );
        assertArrayEquals( "<svg/>".getBytes( StandardCharsets.UTF_8 ), captor.getValue().getData().read() );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.setContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setFormFragmentIcon.js" );

        final ArgumentCaptor<SetDynamicContentSchemaIconParams> captor = ArgumentCaptor.forClass( SetDynamicContentSchemaIconParams.class );
        verify( dynamicSchemaService ).setContentSchemaIcon( captor.capture() );
        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.FORM_FRAGMENT, captor.getValue().getType() );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.setContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setMixinIcon.js" );

        final ArgumentCaptor<SetDynamicContentSchemaIconParams> captor = ArgumentCaptor.forClass( SetDynamicContentSchemaIconParams.class );
        verify( dynamicSchemaService ).setContentSchemaIcon( captor.capture() );
        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.MIXIN, captor.getValue().getType() );
    }

    @Test
    void testPart()
    {
        when( dynamicSchemaService.setComponentIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setPartIcon.js" );

        final ArgumentCaptor<SetDynamicComponentIconParams> captor = ArgumentCaptor.forClass( SetDynamicComponentIconParams.class );
        verify( dynamicSchemaService ).setComponentIcon( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue().getKey() );
        assertEquals( DynamicComponentType.PART, captor.getValue().getType() );
    }

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.setMacroIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/setMacroIcon.js" );

        final ArgumentCaptor<SetDynamicMacroIconParams> captor = ArgumentCaptor.forClass( SetDynamicMacroIconParams.class );
        verify( dynamicSchemaService ).setMacroIcon( captor.capture() );
        assertEquals( MacroKey.from( "myapp:mymacro" ), captor.getValue().getKey() );
    }

    @Test
    void testUnsupportedType()
    {
        runFunction( "/test/SetDynamicIconHandlerTest.js", "setIconOfUnsupportedType" );
    }
}
