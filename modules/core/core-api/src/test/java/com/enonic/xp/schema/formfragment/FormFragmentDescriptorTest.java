package com.enonic.xp.schema.formfragment;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormFragmentDescriptorTest
{

    @Test
    public void mixinBuilderTest()
    {
        final Form.Builder formBuilder = Form.create();
        formBuilder.addFormItem( Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() );

        FormFragmentDescriptor
            mixin1 = FormFragmentDescriptor.create().name( FormFragmentName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        FormFragmentDescriptor mixin2 = FormFragmentDescriptor.create( mixin1 ).build();
        assertEquals( mixin1.getForm(), mixin2.getForm() );
    }

}
