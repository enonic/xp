package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;

public class CheckBoxYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CHECK_BOX;

    public String alignment;

    public CheckBoxYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( alignment != null )
        {
            configBuilder.property( InputTypeProperty.create( "alignment", new StringPropertyValue( alignment ) ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
