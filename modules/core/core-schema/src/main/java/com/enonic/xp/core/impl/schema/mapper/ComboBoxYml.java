package com.enonic.xp.core.impl.schema.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.ObjectPropertyValue;
import com.enonic.xp.inputtype.PropertyValue;
import com.enonic.xp.inputtype.StringPropertyValue;

public class ComboBoxYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.COMBO_BOX;

    public List<OptionYml> options;

    public Map<String, PropertyValue> config;

    public ComboBoxYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

//        if ( options != null )
//        {
//            options.forEach( option -> {
//                final Map<String, PropertyValue> optionConfig = new LinkedHashMap<>();
//                optionConfig.put( "option", new StringPropertyValue( option.text ) );
//                optionConfig.put( "value", new StringPropertyValue(  option.value ) );
//                option.getAttributes().forEach((k, v) -> optionConfig.put( k, new StringPropertyValue( v ) ) );
//
//                final ObjectPropertyValue optionPropertyValue = new ObjectPropertyValue( optionConfig );
//
//
//                final InputTypeProperty.Builder propertyBuilder =
//                    InputTypeProperty.create( "option", new StringPropertyValue( option.text ) );
//                propertyBuilder.attribute( "value", new StringPropertyValue( option.value ) );
//                configBuilder.property( propertyBuilder.build() );
//            } );
//        }

        if ( config != null )
        {
            config.forEach( ( key, value ) -> configBuilder.property( InputTypeProperty.create( key, value ).build() ) );
        }

        builder.inputTypeConfig( configBuilder.build() );
    }
}
