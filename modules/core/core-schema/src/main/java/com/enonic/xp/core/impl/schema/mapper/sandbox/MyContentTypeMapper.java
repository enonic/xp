package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = MyContentTypeMapper.Builder.class)
public class MyContentTypeMapper
{
    private final String name;

    private final String displayName;

    private final String displayNameI18nKey;

    private final String displayNameExpression;

    private MyContentTypeMapper( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.displayNameExpression = builder.displayNameExpression;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDisplayNameExpression()
    {
        return displayNameExpression;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder
    {
        private String name;

        private String displayName;

        private String displayNameI18nKey;

        private String displayNameExpression;

        @JsonProperty("name")
        public Builder setName( final String name )
        {
            this.name = name;
            return this;
        }

        @JsonProperty("displayName")
        public Builder setDisplayName( final DisplayName value )
        {
            this.displayName = value.text;
            this.displayNameI18nKey = value.i18n;
            this.displayNameExpression = value.expression;
            return this;
        }

        public MyContentTypeMapper build()
        {
            return new MyContentTypeMapper( this );
        }
    }

    public static class DisplayName
    {
        @JsonProperty("text")
        String text;

        @JsonProperty("i18n")
        String i18n;

        @JsonProperty("expression")
        String expression;
    }
}
