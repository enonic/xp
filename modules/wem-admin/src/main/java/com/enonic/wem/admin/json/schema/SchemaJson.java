package com.enonic.wem.admin.json.schema;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "schemaKind")
@JsonSubTypes({@JsonSubTypes.Type(value = ContentTypeSummaryJson.class, name = "ContentTypeSummary"),
                  @JsonSubTypes.Type(value = ContentTypeJson.class, name = "ContentType"),
                  @JsonSubTypes.Type(value = RelationshipTypeJson.class, name = "RelationshipType"),
                  @JsonSubTypes.Type(value = MixinJson.class, name = "Mixin")})
public class SchemaJson
    implements ItemJson
{
    private final String key;

    private final String name;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final String iconUrl;

    private final boolean hasChildren;

    public static SchemaJson from( final Schema schema )
    {
        if ( schema instanceof Mixin )
        {
            return new MixinJson( (Mixin) schema );
        }
        else if ( schema instanceof ContentType )
        {
            return new ContentTypeSummaryJson( (ContentType) schema );
        }
        else
        {
            return new RelationshipTypeJson( (RelationshipType) schema );
        }
    }

    protected SchemaJson( final Schema schema )
    {
        this.key = schema.getSchemaKey().toString();
        this.name = schema.getName();
        this.displayName = schema.getDisplayName();
        this.createdTime = schema.getCreatedTime();
        this.modifiedTime = schema.getModifiedTime();
        this.iconUrl = SchemaImageUriResolver.resolve( schema.getSchemaKey() );
        this.hasChildren = schema.hasChildren();
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getCreatedTime()
    {
        return DateTimeFormatter.format( createdTime );
    }

    public String getModifiedTime()
    {
        return DateTimeFormatter.format( modifiedTime );
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }

    @Override
    public boolean getEditable()
    {
        return true;
    }

    @Override
    public boolean getDeletable()
    {
        return true;
    }
}
