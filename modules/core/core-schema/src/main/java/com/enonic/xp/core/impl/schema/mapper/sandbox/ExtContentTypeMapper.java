package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;

@JsonDeserialize(builder = ContentType.Builder.class)
public abstract class ExtContentTypeMapper
{
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder
    {
        @JsonCreator
        public static ContentType.Builder create()
        {
            return ContentType.create();
        }

        @JsonProperty("name")
        public abstract ContentType.Builder name( String name );

        @JsonProperty("form")
        @JsonDeserialize(using = ExtFormDeserializer.class)
        public abstract ContentType.Builder form( Form form );

        public abstract ContentType.Builder displayNameLabel( String label );

        public abstract ContentType.Builder displayNameLabelI18nKey( String key );

        public abstract ContentType.Builder displayNameExpression( String expr );

        @JsonProperty("displayName")
        public Builder setDisplayName( DisplayName displayName )
        {
            if ( displayName != null )
            {
                displayNameLabel( displayName.text );
                displayNameLabelI18nKey( displayName.i18n );
                displayNameExpression( displayName.expression );
            }
            return this;
        }
    }

    public static class DisplayName
    {
        public String text;

        public String i18n;

        public String expression;
    }
}
