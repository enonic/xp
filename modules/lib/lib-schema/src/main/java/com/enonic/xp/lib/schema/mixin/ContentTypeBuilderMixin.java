package com.enonic.xp.lib.schema.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

@JsonPOJOBuilder
public abstract class ContentTypeBuilderMixin
{
    @JsonCreator
    public static ContentType.Builder create()
    {
        return ContentType.create();
    }

    @JsonProperty("name")
    abstract ContentType.Builder name( final ContentTypeName name );

    @JsonProperty("name")
    abstract ContentType.Builder name( final String name );

    @JsonProperty("isAbstract")
    abstract ContentType.Builder setAbstract( final boolean value );

    @JsonProperty("isFinal")
    abstract ContentType.Builder setFinal( final boolean value );

    @JsonProperty("allowChildContent")
    abstract ContentType.Builder allowChildContent( final boolean value );

    @JsonProperty("isBuiltIn")
    abstract ContentType.Builder setBuiltIn( final boolean value );

    @JsonProperty("superType")
    abstract ContentType.Builder superType( final ContentTypeName name );

    @JsonProperty("displayNameLabel")
    abstract ContentType.Builder displayNameLabel( final String value );

    @JsonProperty("displayNameLabelI18nKey")
    abstract ContentType.Builder displayNameLabelI18nKey( final String value );

    @JsonProperty("displayNameExpression")
    abstract ContentType.Builder displayNameExpression( final String value );

    @JsonProperty("form")
    abstract ContentType.Builder addFormItems( final List<FormItem> items );

    @JsonProperty("displayName")
    abstract ContentType.Builder displayName( final String displayName );

    @JsonProperty("displayNameI18nKey")
    abstract ContentType.Builder displayNameI18nKey( final String displayNameI18nKey );

    @JsonProperty("description")
    abstract ContentType.Builder description( final String description );

    @JsonProperty("descriptionI18nKey")
    abstract ContentType.Builder descriptionI18nKey( final String descriptionI18nKey );
}
