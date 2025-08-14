package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;

@JsonPOJOBuilder(withPrefix = "")
public abstract class FormOptionSetBuilderMixin
{
    @JsonCreator
    public static FormOptionSet.Builder create()
    {
        return FormOptionSet.create();
    }

    @JsonProperty("options")
    abstract FormOptionSet.Builder addOptionSetOptions( Iterable<FormOptionSetOption> setOptions );
}
