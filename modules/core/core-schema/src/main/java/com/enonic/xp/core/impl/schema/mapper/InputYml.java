package com.enonic.xp.core.impl.schema.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

public class InputYml
{
    private static final Set<String> RESERVED_VALUES = Set.of( "type", "name", "label", "helpText", "occurrences" );

    public String type;

    public String name;

    public LocalizedText label;

    public LocalizedText helpText;

    public Occurrences occurrences;

    public Map<String, GenericValue> attributes;

    @JsonIgnore
    private final Map<String, GenericValue> topLevelAttributes = new LinkedHashMap<>();

    @JsonAnySetter
    public void addAttribute( final String key, final GenericValue value )
    {
        if ( !RESERVED_VALUES.contains( key ) )
        {
            topLevelAttributes.put( key, value );
        }
    }

    @JsonAnyGetter
    public Map<String, GenericValue> getTopLevelAttributes()
    {
        return topLevelAttributes;
    }

    public final Input convertToInput()
    {
        final Input.Builder builder = Input.create().name( name ).inputType( InputTypeName.from( type ) );

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

        topLevelAttributes.forEach( builder::inputTypeProperty );

        if ( attributes != null )
        {
            for ( Map.Entry<String, GenericValue> entry : attributes.entrySet() )
            {
                final String attribute = entry.getKey();
                final GenericValue value = entry.getValue();
                if ( !topLevelAttributes.containsKey( attribute ) )
                {
                    builder.inputTypeProperty( attribute, value );
                }
            }
        }

        return builder.build();
    }
}
