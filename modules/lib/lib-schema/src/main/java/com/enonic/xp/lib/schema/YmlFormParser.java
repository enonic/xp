package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public final class YmlFormParser
{
    private static final AtomicInteger FIELD_SET_COUNTER = new AtomicInteger( 1 );

    private final ApplicationKey applicationKey;

    public YmlFormParser( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
    }

    public Form parse( final List<Map<String, Object>> source )
    {
        final Form.Builder builder = Form.create();

        if ( source != null )
        {
            doParse( source, builder );
        }

        return builder.build();
    }

    private void doParse( final List<Map<String, Object>> source, final Form.Builder builder )
    {
        if ( source == null )
        {
            return;
        }

        builder.addFormItems( source.stream().map( this::parseFormItem ).collect( Collectors.toList() ) );
    }

    @SuppressWarnings("unchecked")
    private FormItem parseFormItem( final Map<String, Object> source )
    {
        final String type = Objects.requireNonNull( (String) source.get( "type" ), "Form time can not be null" ).trim();

        switch ( type )
        {
            case "FieldSet" ->
            {
                final FieldSet.Builder builder = FieldSet.create();
                builder.name( "fieldSet" + FIELD_SET_COUNTER.getAndIncrement() );

                final Map<String, Object> sourceLabel = (Map<String, Object>) source.get( "label" );
                builder.label( (String) sourceLabel.get( "text" ) );
                builder.labelI18nKey( (String) sourceLabel.get( "i18n" ) );

                final List<Map<String, Object>> sourceItems = (List<Map<String, Object>>) source.get( "items" );

                if ( sourceItems != null )
                {
                    builder.addFormItems( sourceItems.stream().map( this::parseFormItem ).collect( Collectors.toList() ) );
                }

                return builder.build();
            }
            case "Mixin" ->
            {
                final InlineMixin.Builder builder = InlineMixin.create();
                return builder.build();
            }
            case "ItemSet" ->
            {
                final FormItemSet.Builder builder = FormItemSet.create();
                return builder.build();
            }
            case "OptionSet" ->
            {
                final FormOptionSet.Builder builder = FormOptionSet.create();
                return builder.build();
            }
            default ->
            {
                final Input.Builder builder = Input.create();

                final InputTypeName inputTypeName = InputTypeName.from( type );

                builder.inputType( inputTypeName );
                builder.name( (String) source.get( "name" ) );

                final Map<String, Object> sourceLabel = (Map<String, Object>) source.get( "label" );
                builder.label( (String) sourceLabel.get( "text" ) );
                builder.labelI18nKey( (String) sourceLabel.get( "i18n" ) );

                builder.customText( (String) sourceLabel.get( "customText" ) );

                final Map<String, Object> sourceHelpText = (Map<String, Object>) source.get( "label" );
                builder.helpText( (String) sourceHelpText.get( "text" ) );
                builder.helpTextI18nKey( (String) sourceHelpText.get( "i18n" ) );

                final Map<String, Object> sourceOccurrences = (Map<String, Object>) source.get( "occurrences" );
                if ( sourceOccurrences == null )
                {
                    builder.occurrences( Occurrences.create( 0, 1 ) );
                }
                else
                {
                    final int min = Objects.requireNonNullElse( (Integer) sourceOccurrences.get( "minimum" ), 0 );
                    final int max = Objects.requireNonNullElse( (Integer) sourceOccurrences.get( "maximum" ), 0 );
                    builder.occurrences( Occurrences.create( min, max ) );
                }


                builder.immutable( Objects.requireNonNullElse( (Boolean) source.get( "immutable" ), false ) );
                builder.indexed( Objects.requireNonNullElse( (Boolean) source.get( "indexed" ), false ) );
                builder.validationRegexp( (String) source.get( "validationRegexp" ) );
                builder.maximizeUIInputWidth( Objects.requireNonNullElse( (Boolean) source.get( "maximize" ), true ) );

//                final Map<String, Object> sourceDefault = (Map<String, Object>) source.get( "default" );
//                if ( sourceDefault != null )
//                {
//                    final InputTypeDefault.Builder defaultBuilder = InputTypeDefault.create();
//                    builder.defaultValue( defaultBuilder.build() );
//                }
//
//                final Map<String, String> sourceConfig = (Map<String, String>) source.get( "config" );
//
//                if ( sourceConfig != null )
//                {
//                    final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
//                    sourceConfig.forEach( ( key, value ) -> configBuilder.property( InputTypeProperty.create( key, value ).build() ) );
//                    builder.inputTypeConfig( configBuilder.build() );
//                }

                return builder.build();
            }
        }
    }
}
