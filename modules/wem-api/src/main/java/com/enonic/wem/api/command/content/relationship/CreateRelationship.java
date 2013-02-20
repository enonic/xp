package com.enonic.wem.api.command.content.relationship;


import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class CreateRelationship
    extends Command<RelationshipId>
{
    private QualifiedRelationshipTypeName type;

    private ContentId fromContent;

    private ContentId toContent;

    private Map<String, String> properties = Maps.newLinkedHashMap();

    private boolean managed;

    private EntryPath managingData;

    public QualifiedRelationshipTypeName getType()
    {
        return type;
    }

    public CreateRelationship type( final QualifiedRelationshipTypeName type )
    {
        this.type = type;
        return this;
    }

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public CreateRelationship fromContent( final ContentId fromContent )
    {
        this.fromContent = fromContent;
        return this;
    }

    public ContentId getToContent()
    {
        return toContent;
    }

    public CreateRelationship toContent( final ContentId toContent )
    {
        this.toContent = toContent;
        return this;
    }

    public CreateRelationship property( final String key, final String value )
    {
        this.properties.put( key, value );
        return this;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public boolean isManaged()
    {
        return managed;
    }

    public CreateRelationship managed( EntryPath managingData )
    {
        this.managed = true;
        this.managingData = managingData;
        return this;
    }

    public EntryPath getManagingData()
    {
        return managingData;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( fromContent, "fromContent cannot be null" );
        Preconditions.checkNotNull( toContent, "toContent cannot be null" );
        if ( managed )
        {
            Preconditions.checkNotNull( managingData, "managingData cannot be null" );
        }
    }
}
