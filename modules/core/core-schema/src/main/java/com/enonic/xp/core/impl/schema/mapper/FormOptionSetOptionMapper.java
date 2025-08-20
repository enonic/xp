package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.schema.I18NText;

@JsonDeserialize(builder = FormOptionSetOption.Builder.class)
public abstract class FormOptionSetOptionMapper
{
    @JsonIgnoreProperties({"type"})
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static FormOptionSetOption.Builder create()
        {
            return FormOptionSetOption.create();
        }

        @JsonProperty("items")
        abstract FormOptionSetOption.Builder addFormItems( Iterable<FormItem> formItems );

        @JsonProperty("label")
        abstract FormOptionSetOption.Builder setLabel( I18NText value );

        @JsonProperty("helpText")
        abstract FormOptionSetOption.Builder setHelpText( I18NText value );

    }
}
