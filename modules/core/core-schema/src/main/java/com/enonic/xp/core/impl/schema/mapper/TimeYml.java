package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class TimeYml
    extends InputYml
{
    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.TIME;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        // do nothing
    }
}
