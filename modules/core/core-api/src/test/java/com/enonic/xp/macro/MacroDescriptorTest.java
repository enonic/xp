package com.enonic.xp.macro;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MacroDescriptorTest
{
    @Test
    void testCreate()
    {
        FieldSet body = FieldSet.create().
            label( "Body" ).
            addFormItem( Input.create().name( "param1" ).label( "Parameter 1" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        final Form config = Form.create().addFormItem( body ).build();

        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my_app:macro1" ) ).
            description( "my description" ).
            title( "my display name" ).
            form( config ).
            icon( Icon.from( new byte[]{123}, "image/png", Instant.now() ) ).
            build();

        assertEquals( "my_app:macro1", macroDescriptor1.getKey().toString() );
        assertEquals( "macro1", macroDescriptor1.getName() );
        assertEquals( "my display name", macroDescriptor1.getTitle() );
        assertEquals( "my description", macroDescriptor1.getDescription() );
        assertEquals( config, macroDescriptor1.getForm() );
    }
}
