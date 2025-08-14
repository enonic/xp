package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;

@JsonPOJOBuilder(withPrefix = "")
public abstract class FieldSetBuilderMixin
{
    @JsonCreator
    public static FieldSet.Builder create()
    {
        return FieldSet.create();
    }

    @JsonProperty("items")
    abstract FieldSet.Builder addFormItems( Iterable<FormItem> items );
}
