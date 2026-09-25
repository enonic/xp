package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static com.enonic.xp.lib.schema.SetDynamicIconHandlerTest.ICON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetDynamicIconHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( dynamicSchemaService.getContentTypeIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getContentTypeIcon.js" );

        final ArgumentCaptor<ContentTypeName> captor = ArgumentCaptor.forClass( ContentTypeName.class );
        verify( dynamicSchemaService ).getContentTypeIcon( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue() );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.getFormFragmentIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getFormFragmentIcon.js" );

        final ArgumentCaptor<FormFragmentName> captor = ArgumentCaptor.forClass( FormFragmentName.class );
        verify( dynamicSchemaService ).getFormFragmentIcon( captor.capture() );
        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue() );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.getMixinIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getMixinIcon.js" );

        final ArgumentCaptor<MixinName> captor = ArgumentCaptor.forClass( MixinName.class );
        verify( dynamicSchemaService ).getMixinIcon( captor.capture() );
        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue() );
    }

    @Test
    void testPart()
    {
        when( dynamicSchemaService.getPartIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getPartIcon.js" );

        final ArgumentCaptor<DescriptorKey> captor = ArgumentCaptor.forClass( DescriptorKey.class );
        verify( dynamicSchemaService ).getPartIcon( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue() );
    }

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.getMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getMacroIcon.js" );

        verify( dynamicSchemaService ).getMacroIcon( MacroKey.from( "myapp:mymacro" ) );
    }

    @Test
    void testNotFound()
    {
        when( dynamicSchemaService.getMacroIcon( any() ) ).thenReturn( null );

        runFunction( "/test/GetDynamicIconHandlerTest.js", "getMissingIcon" );
    }
}
