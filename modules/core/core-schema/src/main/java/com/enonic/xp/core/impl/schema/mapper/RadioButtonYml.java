package com.enonic.xp.core.impl.schema.mapper;

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

public class RadioButtonYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.RADIO_BUTTON;

    public List<OptionYml> options;

    public RadioButtonYml()
    {
        super( INPUT_TYPE_NAME );
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( options != null )
        {
            final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();

            if ( options != null )
            {
                options.forEach( option -> {
                    final Map<String, PropertyValue> optionMap = new LinkedHashMap<>();

                    optionMap.put( "value", new StringPropertyValue( option.value ) );
                    if ( option.label != null )
                    {
                        final Map<String, PropertyValue> optionTextMap = new LinkedHashMap<>();
                        optionTextMap.put( "text", new StringPropertyValue( option.label.text() ) );
                        if ( option.label.i18n() != null )
                        {
                            optionTextMap.put( "i18n", new StringPropertyValue( option.label.i18n() ) );
                        }
                        optionMap.put( "label", new ObjectPropertyValue( optionTextMap ) );
                    }

                    optionMap.putAll( option.getAttributes() );

                    final InputTypeProperty.Builder propertyBuilder =
                        InputTypeProperty.create( "option", new ObjectPropertyValue( optionMap ) );
                    configBuilder.property( propertyBuilder.build() );
                } );
            }

            builder.inputTypeConfig( configBuilder.build() );
        }
    }

}
