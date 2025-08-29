package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class DateYml
    extends InputYml
{
    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.DATE;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        // do nothing
    }
}
