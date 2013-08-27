package com.enonic.wem.core.schema.content.form.inputtype;


import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.core.plugin.ext.ExtensionPointTest;


public class InputTypeExtensionsTest
    extends ExtensionPointTest<InputType, InputTypeExtensions>
{
    public InputTypeExtensionsTest()
    {
        super( InputType.class );
    }

    @Override
    protected InputTypeExtensions createExtensionPoint()
    {
        return new InputTypeExtensions();
    }

    @Override
    protected InputType createOne()
    {
        return InputTypes.TEXT_AREA;
    }

    @Override
    protected InputType createTwo()
    {
        return InputTypes.TEXT_LINE;
    }
}
