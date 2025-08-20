package com.enonic.xp.core.impl.schema.mapper;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public final class InputRegistry
{
    private static final Map<Class<?>, InputFactory<?>> FACTORIES = new HashMap<>();

    static
    {
        register( RadioButtonYml.class, yml -> {
            final Input.Builder builder =
                Input.create().name( yml.name ).label( yml.label.text() ).inputType( InputTypeName.from( "RadioButton" ) );

            if ( yml.defaultValue != null )
            {
                builder.defaultValue(
                    InputTypeDefault.create().property( InputTypeProperty.create( "default", yml.defaultValue ).build() ).build() );
            }

            if ( yml.options != null )
            {
                yml.options.forEach( option -> {
                    final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( option.name, option.value );
                    option.getAttributes().forEach( propertyBuilder::attribute );
                    builder.inputTypeConfig( InputTypeConfig.create().property( propertyBuilder.build() ).build() );
                } );
            }
            return builder.build();
        } );

        register( TextLineYml.class, yml -> {
            final Input.Builder builder = Input.create().name( yml.name ).label( yml.label.text() ).inputType( InputTypeName.from( "TextLine" ) );

            if ( yml.defaultValue != null )
            {
                builder.defaultValue(
                    InputTypeDefault.create().property( InputTypeProperty.create( "default", yml.defaultValue ).build() ).build() );
            }

            if ( yml.maxLength != null )
            {
                builder.inputTypeConfig( InputTypeConfig.create()
                                             .property( InputTypeProperty.create( "maxLength", yml.maxLength.toString() ).build() )
                                             .build() );
            }

            if ( yml.regexp != null )
            {
                builder.inputTypeConfig(
                    InputTypeConfig.create().property( InputTypeProperty.create( "regexp", yml.regexp ).build() ).build() );
            }

            return builder.build();
        } );
    }

    public static <T extends InputYml> void register( final Class<T> type, final InputFactory<T> factory )
    {
        FACTORIES.put( type, factory );
    }

    @SuppressWarnings("unchecked")
    public static Input toInput( final InputYml yaml )
    {
        InputFactory<InputYml> factory = (InputFactory<InputYml>) FACTORIES.get( yaml.getClass() );
        if ( factory == null )
        {
            throw new IllegalArgumentException( "No factory for " + yaml.getClass() );
        }
        return factory.build( yaml );
    }
}
