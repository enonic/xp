package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;

@JsonDeserialize(builder = FieldSet.Builder.class)
public abstract class FieldSetMapper
{
    @JsonIgnoreProperties({"type"})
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static FieldSet.Builder create()
        {
            return FieldSet.create();
        }

        @JsonProperty("items")
        abstract FieldSet.Builder addFormItems( Iterable<FormItem> items );
    }
}
