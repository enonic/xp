package com.enonic.xp.core.impl.schema.mapper;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;
import com.enonic.xp.schema.LocalizedText;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class InputYml
{
    private final InputTypeName inputTypeName;

    public String type;

    public String name;

    public LocalizedText label;

    public LocalizedText helpText;

    public Occurrences occurrences;

    @JsonProperty("default")
    public Object defaultValue;

    protected InputYml( final InputTypeName inputTypeName )
    {
        this.inputTypeName = Objects.requireNonNull( inputTypeName, "inputTypeName can not be null" );
    }

    public final Input convertToInput()
    {
        final Input.Builder builder = Input.create().name( name ).inputType( inputTypeName );

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
            // TODO maybe default should be moved to an implementation
            builder.defaultValue( InputTypeDefault.create()
                                      .property( InputTypeProperty.create( "default", new StringPropertyValue( defaultValue.toString() ) )
                                                     .build() )
                                      .build() );
        }

        customizeInputType( builder );

        return builder.build();
    }

    public void customizeInputType( Input.Builder builder )
    {
    }
}
