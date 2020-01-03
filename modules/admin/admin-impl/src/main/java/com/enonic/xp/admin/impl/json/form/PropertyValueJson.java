package com.enonic.xp.admin.impl.json.form;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.Value;

public class PropertyValueJson
{
    @JsonProperty("value")
    private Object value;

    @JsonProperty("type")
    private String type;

    PropertyValueJson( final Value value )
    {
        this.value = value.getObject();
        this.type = value.getType().getName();
    }
}
