package com.enonic.xp.schema.mixin;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MixinTest
{

    @Test
    void mixinBuilderTest()
    {
        final Form.Builder formBuilder = Form.create();
        formBuilder.addFormItem( Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() );

        Mixin mixin1 = Mixin.create().name( MixinName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        Mixin mixin2 = Mixin.create( mixin1 ).build();
        assertEquals( mixin1.getForm(), mixin2.getForm() );
    }

}
