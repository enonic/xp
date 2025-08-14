package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;

@JsonDeserialize(builder = FormOptionSet.Builder.class)
public abstract class FormOptionSetMixin
{
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static FormOptionSet.Builder create()
        {
            return FormOptionSet.create();
        }

        @JsonProperty("options")
        @JsonDeserialize(using = FormOptionSetOptionsDeserializer.class)
        abstract FormOptionSet.Builder addOptionSetOptions( Iterable<FormOptionSetOption> setOptions );
    }
}
