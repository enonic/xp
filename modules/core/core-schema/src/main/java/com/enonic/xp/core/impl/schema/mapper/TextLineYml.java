package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class TextLineYml
    extends InputYml
{
    public Integer maxLength;

    public String regexp;

    @JsonProperty("default")
    public String defaultValue;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.TEXT_LINE;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( defaultValue != null )
        {
            builder.defaultValue(
                InputTypeDefault.create().property( InputTypeProperty.create( "default", defaultValue ).build() ).build() );
        }

        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( maxLength != null )
        {
            configBuilder.property( InputTypeProperty.create( "maxLength", maxLength.toString() ).build() );
        }

        if ( regexp != null )
        {
            configBuilder.property( InputTypeProperty.create( "regexp", regexp ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
