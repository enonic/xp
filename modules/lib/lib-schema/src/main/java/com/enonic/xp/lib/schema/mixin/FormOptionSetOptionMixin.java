package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormOptionSetOption;

@JsonDeserialize(builder = FormOptionSetOption.Builder.class)
public abstract class FormOptionSetOptionMixin
{
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder
    {
        @JsonCreator
        public static FormOptionSetOption.Builder create()
        {
            return FormOptionSetOption.create();
        }

        @JsonProperty("items")
        abstract FormOptionSetOption.Builder addFormItems( Iterable<FormItem> formItems );

    }
}
