package com.enonic.xp.core.impl.schema.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RadioButtonYml
    extends InputYml
{
    public List<Option> options;

    @JsonProperty("default")
    public String defaultValue;

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
