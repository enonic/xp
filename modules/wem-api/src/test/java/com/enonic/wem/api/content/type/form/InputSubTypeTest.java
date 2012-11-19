package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.Input.newInput;


public class InputSubTypeTest
{
    private Module module = Module.newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        InputSubType inputSubType = InputSubType.newInputSubType().module( module ).input( input ).build();
    }
}
