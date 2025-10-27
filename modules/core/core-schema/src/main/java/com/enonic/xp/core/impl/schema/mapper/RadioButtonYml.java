package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class RadioButtonYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.RADIO_BUTTON;

    public List<OptionYml> options;

    public RadioButtonYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        InputTypeHelper.populateOptions( options, builder );
    }
}
