package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteDynamicIconHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( dynamicSchemaService.deleteContentTypeIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteContentTypeIcon.js" );

        final ArgumentCaptor<ContentTypeName> captor = ArgumentCaptor.forClass( ContentTypeName.class );
        verify( dynamicSchemaService ).deleteContentTypeIcon( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue() );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.deleteFormFragmentIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteFormFragmentIcon.js" );

        final ArgumentCaptor<FormFragmentName> captor = ArgumentCaptor.forClass( FormFragmentName.class );
        verify( dynamicSchemaService ).deleteFormFragmentIcon( captor.capture() );
        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue() );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.deleteMixinIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMixinIcon.js" );

        final ArgumentCaptor<MixinName> captor = ArgumentCaptor.forClass( MixinName.class );
        verify( dynamicSchemaService ).deleteMixinIcon( captor.capture() );
        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue() );
    }

    @Test
    void testPart()
    {
        when( dynamicSchemaService.deletePartIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePartIcon.js" );

        final ArgumentCaptor<DescriptorKey> captor = ArgumentCaptor.forClass( DescriptorKey.class );
        verify( dynamicSchemaService ).deletePartIcon( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue() );
    }

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacroIcon.js" );

        verify( dynamicSchemaService ).deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) );
    }
}
