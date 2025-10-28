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

        XData xData1 = XData.create().name( XDataName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        XData xData2 = XData.create( xData1 ).build();
        assertEquals( xData1.getForm(), xData2.getForm() );
    }

}
