package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;

public class MyContentTypeMapper2
{
    @JsonProperty("name")
    public String name;

    @JsonProperty("form")
    public Form form;

    @JsonProperty("displayName")
    public DisplayName displayName;

    public static class DisplayName
    {
        @JsonProperty("text")
        public String text;

        @JsonProperty("i18n")
        public String i18n;

        @JsonProperty("expression")
        public String expression;
    }

    public ContentType.Builder toContentTypeBuilder()
    {
        return ContentType.create()
            .displayName( displayName.text )
            .displayNameI18nKey( displayName.i18n )
            .displayNameExpression( displayName.expression )
            .form( form );
    }
}
