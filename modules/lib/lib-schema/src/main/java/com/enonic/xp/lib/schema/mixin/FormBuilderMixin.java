package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;

@JsonPOJOBuilder
public abstract class FormBuilderMixin
{
    @JsonCreator
    public static Form.Builder create()
    {
        return Form.create();
    }

    @JsonProperty("form")
    abstract Form.Builder addFormItem( FormItem item );
}
