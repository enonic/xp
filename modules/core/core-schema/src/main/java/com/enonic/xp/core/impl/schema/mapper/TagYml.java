package com.enonic.xp.core.impl.schema.mapper;

import java.util.Map;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.PropertyValue;

public class TagYml
    extends InputYml
{

    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TAG;

    public Map<String, PropertyValue> config;

    public TagYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

        if ( config != null )
        {
            config.forEach( ( name, value ) -> configBuilder.property( InputTypeProperty.create( name, value ).build() ) );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
