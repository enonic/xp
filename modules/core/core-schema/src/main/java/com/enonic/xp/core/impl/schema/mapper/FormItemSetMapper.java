package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;

@JsonDeserialize(builder = FormItemSet.Builder.class)
public abstract class FormItemSetMapper
{
    @JsonIgnoreProperties({"type"})
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static FormItemSet.Builder create()
        {
            return FormItemSet.create();
        }

        @JsonProperty("items")
        abstract FormItemSet.Builder addFormItems( Iterable<FormItem> items );
    }
}
