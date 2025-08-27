package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class DoubleYml
    extends InputYml
{
    public Double min;

    public Double max;

    @JsonProperty("default")
    public Double defaultValue;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.DOUBLE;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( defaultValue != null )
        {
            builder.defaultValue(
                InputTypeDefault.create().property( InputTypeProperty.create( "default", defaultValue.toString() ).build() ).build() );
        }

        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
        if ( min != null )
        {
            configBuilder.property( InputTypeProperty.create( "min", min.toString() ).build() );
        }
        if ( max != null )
        {
            configBuilder.property( InputTypeProperty.create( "max", max.toString() ).build() );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
