package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

@JsonDeserialize(builder = ContentType.Builder.class)
public abstract class ContentTypeMapper
{
    @JsonPOJOBuilder
    public abstract static class Builder
    {
        @JsonCreator
        public static ContentType.Builder create()
        {
            return ContentType.create();
        }

        @JsonProperty("name")
        abstract ContentType.Builder name( ContentTypeName name );

        @JsonProperty("name")
        abstract ContentType.Builder name( String name );

        @JsonProperty("isAbstract")
        abstract ContentType.Builder setAbstract( boolean value );

        @JsonProperty("isFinal")
        abstract ContentType.Builder setFinal( boolean value );

        @JsonProperty("allowChildContent")
        abstract ContentType.Builder allowChildContent( boolean value );

        @JsonProperty("isBuiltIn")
        abstract ContentType.Builder setBuiltIn( boolean value );

        @JsonProperty("superType")
        abstract ContentType.Builder superType( ContentTypeName name );

        @JsonProperty("displayNameLabel")
        abstract ContentType.Builder displayNameLabel( String value );

        @JsonProperty("displayNameLabelI18nKey")
        abstract ContentType.Builder displayNameLabelI18nKey( String value );

        @JsonProperty("displayNameExpression")
        abstract ContentType.Builder displayNameExpression( String value );

        @JsonProperty("form")
        abstract ContentType.Builder addFormItems( Iterable<? extends FormItem> items );

        @JsonProperty("displayName")
        abstract ContentType.Builder displayName( String displayName );

        @JsonProperty("displayNameI18nKey")
        abstract ContentType.Builder displayNameI18nKey( String displayNameI18nKey );

        @JsonProperty("description")
        abstract ContentType.Builder description( String description );

        @JsonProperty("descriptionI18nKey")
        abstract ContentType.Builder descriptionI18nKey( String descriptionI18nKey );
    }
}
