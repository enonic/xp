package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.schema.LocalizedText;

@JsonDeserialize(builder = FormOptionSet.Builder.class)
public abstract class FormOptionSetMapper
{
    @JsonIgnoreProperties({"type"})
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

        @JsonProperty("label")
        abstract FormOptionSet.Builder setLabel( LocalizedText value );

        @JsonProperty("helpText")
        abstract FormOptionSet.Builder setHelpText( LocalizedText value );

        @JsonProperty("selected")
        abstract FormOptionSet.Builder multiselection( Occurrences value );
    }
}
