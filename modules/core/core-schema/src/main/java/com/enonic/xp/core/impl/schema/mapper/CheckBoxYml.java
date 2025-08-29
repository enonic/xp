package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class CheckBoxYml
    extends InputYml
{
    public String alignment;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.CHECK_BOX;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( alignment != null )
        {
            configBuilder.property( InputTypeProperty.create( "alignment", alignment ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
