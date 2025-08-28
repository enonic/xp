package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class TextAreaYml
    extends InputYml
{
    public Integer maxLength;

    public Boolean showCounter;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.TEXT_AREA;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( maxLength != null )
        {
            configBuilder.property( InputTypeProperty.create( "maxLength", maxLength.toString() ).build() );
        }

        if ( showCounter != null )
        {
            configBuilder.property( InputTypeProperty.create( "showCounter", showCounter.toString() ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
