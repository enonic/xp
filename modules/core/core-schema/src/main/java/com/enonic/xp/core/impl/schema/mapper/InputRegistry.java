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
            final Input.Builder builder = defaultConverter( yml ).inputType( InputTypeName.from( "RadioButton" ) );

            if ( yml.defaultValue != null )
            {
                builder.defaultValue(
                    InputTypeDefault.create().property( InputTypeProperty.create( "default", yml.defaultValue ).build() ).build() );
            }

            if ( yml.options != null )
            {
                final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
                yml.options.forEach( option -> {
                    final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( "option", option.value );
                    propertyBuilder.attribute( "value", option.name );
                    option.getAttributes().forEach( propertyBuilder::attribute );
                    configBuilder.property( propertyBuilder.build() );
                } );
                builder.inputTypeConfig( configBuilder.build() );
            }
            return builder.build();
        } );

        register( TextLineYml.class, yml -> {
            final Input.Builder builder = defaultConverter( yml ).inputType( InputTypeName.from( "TextLine" ) );

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

        register( DoubleYml.class, yml -> {
            final Input.Builder builder = defaultConverter( yml ).inputType( InputTypeName.from( "Double" ) );

            if ( yml.defaultValue != null )
            {
                builder.defaultValue( InputTypeDefault.create()
                                          .property( InputTypeProperty.create( "default", yml.defaultValue.toString() ).build() )
                                          .build() );
            }

            final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
            if ( yml.min != null )
            {
                configBuilder.property( InputTypeProperty.create( "min", yml.min.toString() ).build() );
            }
            if ( yml.max != null )
            {
                configBuilder.property( InputTypeProperty.create( "max", yml.max.toString() ).build() );
            }

            builder.inputTypeConfig( configBuilder.build() );

            return builder.build();
        } );

        register( ContentSelectorYml.class, yml -> {
            final Input.Builder builder = defaultConverter( yml ).inputType( InputTypeName.from( "ContentSelector" ) );

            final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
            if ( yml.hideToggleIcon != null )
            {
                configBuilder.property( InputTypeProperty.create( "hideToggleIcon", yml.hideToggleIcon.toString() ).build() );
            }
            if ( yml.treeMode != null )
            {
                configBuilder.property( InputTypeProperty.create( "treeMode", yml.treeMode.toString() ).build() );
            }
            if ( yml.allowContentType != null )
            {
                yml.allowContentType.forEach(
                    allowType -> configBuilder.property( InputTypeProperty.create( "allowContentType", allowType ).build() ) );
            }
            if ( yml.allowPath != null )
            {
                yml.allowPath.forEach( allowPath -> configBuilder.property( InputTypeProperty.create( "allowPath", allowPath ).build() ) );
            }

            builder.inputTypeConfig( configBuilder.build() );

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

    private static Input.Builder defaultConverter( final InputYml inputYml )
    {
        final Input.Builder builder = Input.create().name( inputYml.name );

        if ( inputYml.label != null )
        {
            builder.label( inputYml.label.text() ).labelI18nKey( inputYml.label.i18n() );
        }

        if ( inputYml.helpText != null )
        {
            builder.helpText( inputYml.helpText.text() ).helpTextI18nKey( inputYml.helpText.i18n() );
        }

        if ( inputYml.occurrences != null )
        {
            builder.occurrences( inputYml.occurrences );
        }

        return builder;
    }
}
