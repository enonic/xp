package com.enonic.xp.core.impl.schema.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

public class RadioButtonYml
    extends InputYml
{
    public List<Option> options;

    @JsonProperty("default")
    public String defaultValue;

    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.RADIO_BUTTON;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        if ( defaultValue != null )
        {
            builder.defaultValue(
                InputTypeDefault.create().property( InputTypeProperty.create( "default", defaultValue ).build() ).build() );
        }

        if ( options != null )
        {
            final InputTypeConfig.Builder configBuilder = InputTypeConfig.create();
            options.forEach( option -> {
                final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( "option", option.value );
                propertyBuilder.attribute( "value", option.name );
                option.getAttributes().forEach( propertyBuilder::attribute );
                configBuilder.property( propertyBuilder.build() );
            } );
            builder.inputTypeConfig( configBuilder.build() );
        }
    }

    public static class Option
    {
        public String name;

        public String value;

        @JsonIgnore
        private final Map<String, String> attributes = new LinkedHashMap<>();

        @JsonAnySetter
        public void addAttribute( final String key, final String value )
        {
            if ( !Set.of( "name", "value" ).contains( key ) )
            {
                attributes.put( key, value );
            }
        }

        @JsonAnyGetter
        public Map<String, String> getAttributes()
        {
            return attributes;
        }
    }

}
