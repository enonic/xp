package com.enonic.wem.admin.json.schema;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.admin.json.IconJson;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryJson;
import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "schemaKind")
@JsonSubTypes({@JsonSubTypes.Type(value = ContentTypeSummaryJson.class, name = "ContentType"),
                  @JsonSubTypes.Type(value = ContentTypeJson.class, name = "ContentType"),
                  @JsonSubTypes.Type(value = RelationshipTypeJson.class, name = "RelationshipType"),
                  @JsonSubTypes.Type(value = MixinJson.class, name = "Mixin")})
public class SchemaJson
    implements ItemJson
{
    private final String key;

    private final String name;

    private final String displayName;

    private final String description;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final String iconUrl;

    private final IconJson iconJson;

    private final boolean hasChildren;

    protected SchemaJson( final Schema schema, final SchemaIconUrlResolver iconUrlResolver )
    {
        this.key = schema.getSchemaKey() != null ? schema.getSchemaKey().toString() : null;
        this.name = schema.getName() != null ? schema.getName().toString() : null;
        this.displayName = schema.getDisplayName();
        this.description = schema.getDescription();
        this.createdTime = schema.getCreatedTime();
        this.modifiedTime = schema.getModifiedTime();
        this.iconUrl = iconUrlResolver.resolve( schema );
        this.iconJson = schema.getIcon() != null ? new IconJson( schema.getIcon() ) : null;
        this.hasChildren = schema.hasChildren();
    }

    public static SchemaJson from( final Schema schema, final SchemaIconUrlResolver iconUrlResolver )
    {
        if ( schema instanceof Mixin )
        {
            return new MixinJson( (Mixin) schema, iconUrlResolver );
        }
        else if ( schema instanceof ContentType )
        {
            return new ContentTypeSummaryJson( (ContentType) schema, iconUrlResolver );
        }
        else
        {
            return new RelationshipTypeJson( (RelationshipType) schema, iconUrlResolver );
        }
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

    public String getDescription()
    {
        return description;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public IconJson getIcon()
    {
        return this.iconJson;
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
