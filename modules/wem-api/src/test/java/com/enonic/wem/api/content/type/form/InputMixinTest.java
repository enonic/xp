package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.form.Input.newInput;


public class InputMixinTest
{
    private ModuleName module = ModuleName.from( "MyModule" );

    @Test
    public void tags()
    {
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        InputMixin inputMixin = InputMixin.newInputMixin().module( module ).input( input ).build();
    }
}
