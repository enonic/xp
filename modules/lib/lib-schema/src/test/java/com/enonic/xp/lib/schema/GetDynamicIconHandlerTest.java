package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
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
        when( dynamicSchemaService.getContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getContentTypeIcon.js" );

        final ArgumentCaptor<GetDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( GetDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).getContentSchemaIcon( captor.capture() );
        assertEquals( ContentTypeName.from( "myapp:mytype" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.CONTENT_TYPE, captor.getValue().getType() );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.getContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getFormFragmentIcon.js" );

        final ArgumentCaptor<GetDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( GetDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).getContentSchemaIcon( captor.capture() );
        assertEquals( FormFragmentName.from( "myapp:myfragment" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.FORM_FRAGMENT, captor.getValue().getType() );
    }

    @Test
    void testMixin()
    {
        when( dynamicSchemaService.getContentSchemaIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getMixinIcon.js" );

        final ArgumentCaptor<GetDynamicContentSchemaParams> captor = ArgumentCaptor.forClass( GetDynamicContentSchemaParams.class );
        verify( dynamicSchemaService ).getContentSchemaIcon( captor.capture() );
        assertEquals( MixinName.from( "myapp:mymixin" ), captor.getValue().getName() );
        assertEquals( DynamicContentSchemaType.MIXIN, captor.getValue().getType() );
    }

    @Test
    void testPart()
    {
        when( dynamicSchemaService.getComponentIcon( any() ) ).thenReturn( ICON );

        runScript( "/lib/xp/examples/schema/getPartIcon.js" );

        final ArgumentCaptor<GetDynamicComponentParams> captor = ArgumentCaptor.forClass( GetDynamicComponentParams.class );
        verify( dynamicSchemaService ).getComponentIcon( captor.capture() );
        assertEquals( DescriptorKey.from( "myapp:mypart" ), captor.getValue().getKey() );
        assertEquals( DynamicComponentType.PART, captor.getValue().getType() );
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
