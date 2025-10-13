package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormFragment;
import com.enonic.xp.schema.mixin.FormFragmentName;

@JsonDeserialize(builder = FormFragment.Builder.class)
public abstract class FormFragmentMapper
{
    @JsonIgnoreProperties({"type"})
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static FormFragment.Builder create()
        {
            return FormFragment.create();
        }

        @JsonProperty("name")
        abstract FormFragment.Builder formFragment( FormFragmentName formFragmentName );
    }
}
