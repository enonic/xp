package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class ComboBoxYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.COMBO_BOX;

    public List<OptionYml> options;

    public ComboBoxYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        InputTypeHelper.populateOptions( options, builder );
    }
}
