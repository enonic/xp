package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class HtmlAreaYml
    extends InputYml
{
    @JsonProperty("default")
    public String defaultValue;

    public String exclude;

    public String include;

    public String allowHeadings;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.HTML_AREA;
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

        if ( exclude != null )
        {
            configBuilder.property( InputTypeProperty.create( "exclude", exclude ).build() );
        }

        if ( include != null )
        {
            configBuilder.property( InputTypeProperty.create( "include", include ).build() ).build();
        }

        if ( allowHeadings != null )
        {
            configBuilder.property( InputTypeProperty.create( "allowHeadings", allowHeadings ).build() ).build();
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
