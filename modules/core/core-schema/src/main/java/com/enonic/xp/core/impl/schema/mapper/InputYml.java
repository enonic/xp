package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.LocalizedText;

public abstract class InputYml
{
    public String type;

    public String name;

    public LocalizedText label;

    public LocalizedText helpText;

    public Occurrences occurrences;

    @JsonProperty("default")
    public Object defaultValue;

    public final Input convertToInput()
    {
        final Input.Builder builder = Input.create().name( name ).inputType( getInputTypeName() );

        if ( label != null )
        {
            builder.label( label.text() ).labelI18nKey( label.i18n() );
        }

        if ( helpText != null )
        {
            builder.helpText( helpText.text() ).helpTextI18nKey( helpText.i18n() );
        }

        if ( occurrences != null )
        {
            builder.occurrences( occurrences );
        }

        if ( defaultValue != null )
        {
            builder.defaultValue(
                InputTypeDefault.create().property( InputTypeProperty.create( "default", defaultValue.toString() ).build() ).build() );
        }

        customizeInputType( builder );

        return builder.build();
    }

    public abstract InputTypeName getInputTypeName();

    public abstract void customizeInputType( Input.Builder builder );
}
