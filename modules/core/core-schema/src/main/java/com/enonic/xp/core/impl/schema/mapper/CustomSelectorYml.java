package com.enonic.xp.core.impl.schema.mapper;

import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class CustomSelectorYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CUSTOM_SELECTOR;

    @JacksonInject("currentApplication")
    private ApplicationKey currentApplication;

    public String service;

    public Map<String, String> params;

    public CustomSelectorYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( service != null )
        {
            configBuilder.property(
                InputTypeProperty.create( "service", new ApplicationRelativeResolver( currentApplication ).toServiceUrl( service ) )
                    .build() );
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
