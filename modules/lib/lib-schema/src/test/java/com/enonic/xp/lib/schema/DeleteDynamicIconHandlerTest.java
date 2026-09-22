package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
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
        when( dynamicSchemaService.deleteContentSchemaIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteContentTypeIcon.js" );

        final ArgumentCaptor<DeleteDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( DeleteDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).deleteContentSchemaIcon( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.CONTENT_TYPE, captor.getValue().getType() );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.deleteContentSchemaIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteFormFragmentIcon.js" );

        final ArgumentCaptor<DeleteDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( DeleteDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).deleteContentSchemaIcon( captor.capture() );
        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.FORM_FRAGMENT, captor.getValue().getType() );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.deleteContentSchemaIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMixinIcon.js" );

        final ArgumentCaptor<DeleteDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( DeleteDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).deleteContentSchemaIcon( captor.capture() );
        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.MIXIN, captor.getValue().getType() );
    }

    @Test
    void testPart()
    {
        when( dynamicSchemaService.deleteComponentIcon( any() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePartIcon.js" );

        final ArgumentCaptor<DeleteDynamicComponentParams> captor = ArgumentCaptor.forClass( DeleteDynamicComponentParams.class );
        verify( dynamicSchemaService ).deleteComponentIcon( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue().getKey() );
        assertEquals( DynamicComponentType.PART, captor.getValue().getType() );
    }

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteMacroIcon.js" );

        verify( dynamicSchemaService ).deleteMacroIcon( MacroKey.from( "myapp:mymacro" ) );
    }
}
