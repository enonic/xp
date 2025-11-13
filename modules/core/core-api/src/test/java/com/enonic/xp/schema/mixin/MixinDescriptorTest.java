package com.enonic.xp.schema.mixin;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MixinDescriptorTest
{
    @Test
    void test()
    {
        final Form.Builder formBuilder = Form.create();
        formBuilder.addFormItem( Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() );

        MixinDescriptor descriptor1 =
            MixinDescriptor.create().name( MixinName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        MixinDescriptor descriptor2 = MixinDescriptor.create( descriptor1 ).build();
        assertEquals( descriptor1.getForm(), descriptor2.getForm() );
    }

}
