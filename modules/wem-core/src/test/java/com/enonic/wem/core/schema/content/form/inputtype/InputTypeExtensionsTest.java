package com.enonic.wem.core.schema.content.form.inputtype;


import com.acme.DummyCustomInputType;
import com.acme.DummyCustomInputType2;

import com.enonic.wem.api.form.inputtype.InputTypeExtension;
import com.enonic.wem.core.plugin.ext.ExtensionPointTest;


public class InputTypeExtensionsTest
    extends ExtensionPointTest<InputTypeExtension, InputTypeExtensionsImpl>
{
    public InputTypeExtensionsTest()
    {
        super( InputTypeExtension.class );
    }

    @Override
    protected InputTypeExtensionsImpl createExtensionPoint()
    {
        return new InputTypeExtensionsImpl();
    }

    @Override
    protected InputTypeExtension createOne()
    {
        return new DummyCustomInputType();
    }

    @Override
    protected InputTypeExtension createTwo()
    {
        return new DummyCustomInputType2();
    }
}
