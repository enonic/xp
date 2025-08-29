package com.enonic.xp.core.impl.schema.mapper;

import java.util.List;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class ComboBoxYml
    extends InputYml
{
    public List<OptionYml> options;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.COMBO_BOX;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( options != null )
        {
            final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
            options.forEach( option -> {
                final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( "option", option.text );
                propertyBuilder.attribute( "value", option.value );
                option.getAttributes().forEach( propertyBuilder::attribute );
                configBuilder.property( propertyBuilder.build() );
            } );
            builder.inputTypeConfig( configBuilder.build() );
        }
    }
}
