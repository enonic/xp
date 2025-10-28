package com.enonic.xp.schema.xdata;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XDataTest
{
    @Test
    void xDataBuilderTest()
    {
        final Form.Builder formBuilder = Form.create();
        formBuilder.addFormItem( Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() );

        MixinDescriptor xData1 = MixinDescriptor.create().name( MixinName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        MixinDescriptor xData2 = MixinDescriptor.create( xData1 ).build();
        assertEquals( xData1.getForm(), xData2.getForm() );
    }

}
