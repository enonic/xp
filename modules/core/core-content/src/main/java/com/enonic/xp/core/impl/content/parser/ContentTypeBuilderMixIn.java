package com.enonic.xp.core.impl.content.parser;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.content.ContentDisplayName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

abstract class ContentTypeBuilderMixIn
{
    @JsonCreator
    public static ContentType.Builder create()
    {
        return ContentType.create();
    }

    @JsonProperty("name")
    public abstract ContentType.Builder name( String name );

    @JsonProperty("form")
    public abstract ContentType.Builder form( Form form );

    @JsonProperty("superType")
    abstract ContentType.Builder superType( ContentTypeName name );

    @JsonProperty("displayName")
    public abstract ContentType.Builder setDisplayName( ContentDisplayName value );

    @JsonProperty("label")
    public abstract ContentType.Builder setLabel( LocalizedText value );

    @JsonProperty("description")
    public abstract ContentType.Builder setDescription( LocalizedText value );

    @JsonProperty("abstract")
    abstract ContentType.Builder setAbstract( boolean value );

    @JsonProperty("final")
    abstract ContentType.Builder setFinal( boolean value );

    @JsonProperty(value = "allowChildContent", defaultValue = "true")
    abstract ContentType.Builder allowChildContent( boolean value );

    @JsonProperty("allowChildContentType")
    abstract ContentType.Builder allowChildContentType( List<String> allowChildContentType );

    @JsonProperty("config")
    @JsonDeserialize(using = InputTypeConfigDeserializer.class)
    abstract ContentType.Builder schemaConfig( final InputTypeConfig config );
}
