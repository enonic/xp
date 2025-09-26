package com.enonic.xp.core.impl.schema.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.inputtype.PropertyValue;
import com.enonic.xp.schema.LocalizedText;

public final class OptionYml
{
    private static final Set<String> RESERVED_VALUES = Set.of( "value", "text" );

    public String value;

    public LocalizedText label;

    @JsonIgnore
    private final Map<String, PropertyValue> attributes = new LinkedHashMap<>();

    @JsonAnySetter
    public void addAttribute( final String key, final PropertyValue value )
    {
        if ( !RESERVED_VALUES.contains( key ) )
        {
            attributes.put( key, value );
        }
    }

    @JsonAnyGetter
    public Map<String, PropertyValue> getAttributes()
    {
        return attributes;
    }
}
