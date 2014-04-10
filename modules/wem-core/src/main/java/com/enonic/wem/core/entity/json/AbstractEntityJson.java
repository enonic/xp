package com.enonic.wem.core.entity.json;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.core.data.json.RootDataSetJson;
import com.enonic.wem.core.entity.EntityPatternIndexConfigJson;
import com.enonic.wem.core.entity.EntityPropertyIndexConfigJson;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = NodeJson.class, name = "Node"), @JsonSubTypes.Type(value = EntityJson.class, name = "Entity")})
public abstract class AbstractEntityJson
{
    protected String id;

    protected DateTime createdTime;

    protected RootDataSetJson data;

    protected DateTime modifiedTime;

    protected EntityIndexConfigJson entityIndexConfig;

    private AttachmentsJson attachments;

    protected AbstractEntityJson( final String id, final DateTime createdTime, final RootDataSetJson data, final DateTime modifiedTime,
                                  final EntityIndexConfigJson entityIndexConfig, final AttachmentsJson attachments )
    {
        this.id = id;
        this.createdTime = createdTime;
        this.data = data;
        this.modifiedTime = modifiedTime;
        this.entityIndexConfig = entityIndexConfig;
        this.attachments = attachments;
    }

    protected AbstractEntityJson( final Entity entity )
    {
        this.id = entity.id().toString();
        this.createdTime = entity.getCreatedTime();
        this.modifiedTime = entity.getModifiedTime();
        this.data = new RootDataSetJson( entity.data() );
        this.entityIndexConfig = createEntityIndexConfig( entity.getEntityIndexConfig() );
        this.attachments = new AttachmentsJson( entity.attachments() );
    }

    private EntityIndexConfigJson createEntityIndexConfig( final EntityIndexConfig indexConfig )
    {
        if ( indexConfig instanceof EntityPropertyIndexConfig )
        {
            return new EntityPropertyIndexConfigJson( (EntityPropertyIndexConfig) indexConfig );
        }
        else if ( indexConfig instanceof EntityPatternIndexConfig )
        {
            return new EntityPatternIndexConfigJson( (EntityPatternIndexConfig) indexConfig );
        }

        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public RootDataSetJson getData()
    {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public EntityIndexConfigJson getEntityIndexConfig()
    {
        return entityIndexConfig;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AttachmentsJson getAttachments()
    {
        return attachments;
    }
}
