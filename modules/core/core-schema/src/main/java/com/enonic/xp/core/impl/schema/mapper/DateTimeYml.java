package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class DateTimeYml
    extends InputYml
{
    public Boolean timezone;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.DATE_TIME;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( timezone != null )
        {
            configBuilder.property( InputTypeProperty.create( "timezone", timezone.toString() ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
