package com.enonic.wem.core.schema.content.form.inputtype;


import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.core.plugin.ext.ExtensionPointTest;


public class InputTypeExtensionsTest
    extends ExtensionPointTest<InputType, InputTypeExtensionsImpl>
{
    public InputTypeExtensionsTest()
    {
        super( InputType.class );
    }

    @Override
    protected InputTypeExtensionsImpl createExtensionPoint()
    {
        return new InputTypeExtensionsImpl();
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
