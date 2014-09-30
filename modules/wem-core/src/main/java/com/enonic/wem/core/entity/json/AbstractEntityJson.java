package com.enonic.wem.core.entity.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.data.RootDataSetJson;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.core.entity.Entity;
import com.enonic.wem.core.entity.PatternBasedIndexConfigDocumentJson;
import com.enonic.wem.core.entity.relationship.IndexConfigDocumentJson;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = NodeJson.class, name = "Node"), @JsonSubTypes.Type(value = EntityJson.class, name = "Entity")})
public abstract class AbstractEntityJson
{
    protected String id;

    protected Instant createdTime;

    protected RootDataSetJson data;

    protected Instant modifiedTime;

    protected IndexConfigDocumentJson indexConfigDocument;

    private AttachmentsJson attachments;

    protected AbstractEntityJson( final String id, final Instant createdTime, final RootDataSetJson data, final Instant modifiedTime,
                                  final IndexConfigDocumentJson indexConfigDocument, final AttachmentsJson attachments )
    {
        this.id = id;
        this.createdTime = createdTime;
        this.data = data;
        this.modifiedTime = modifiedTime;
        this.indexConfigDocument = indexConfigDocument;
        this.attachments = attachments;
    }

    protected AbstractEntityJson( final Entity entity )
    {
        this.id = entity.id().toString();
        this.createdTime = entity.getCreatedTime();
        this.modifiedTime = entity.getModifiedTime();
        this.data = new RootDataSetJson( entity.data() );
        this.indexConfigDocument = createEntityIndexConfig( entity.getIndexConfigDocument() );
        this.attachments = new AttachmentsJson( entity.attachments() );
    }

    private IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getCreatedTime()
    {
        return createdTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public RootDataSetJson getData()
    {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IndexConfigDocumentJson getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AttachmentsJson getAttachments()
    {
        return attachments;
    }
}
