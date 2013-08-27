package com.enonic.wem.core.schema.content.form.inputtype;

import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.core.plugin.ext.ExtensionPoint;

public class InputTypeExtensions
    extends ExtensionPoint<InputType>
{
    public InputTypeExtensions()
    {
        super( InputType.class );
    }

    @Override
    public int compare( final InputType inputTypeA, final InputType inputTypeB )
    {
        return inputTypeA.getName().compareTo( inputTypeB.getName() );
    }
}
