package com.enonic.xp.core.impl.schema.mapper;

import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;

import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class CustomSelectorYml
    extends InputYml
{
    @JacksonInject("applicationRelativeResolver")
    private ApplicationRelativeResolver applicationRelativeResolver;

    public String service;

    public Map<String, String> params;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.CUSTOM_SELECTOR;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( service != null )
        {
            configBuilder.property( InputTypeProperty.create( "service", applicationRelativeResolver.toServiceUrl( service ) ).build() );
        }

        if ( params != null )
        {
            params.forEach( ( key, value ) -> {
                configBuilder.property( InputTypeProperty.create( "param", value ).attribute( "value", key ).build() );
            } );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
