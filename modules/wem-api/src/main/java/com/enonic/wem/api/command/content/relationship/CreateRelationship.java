package com.enonic.wem.api.command.content.relationship;


import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class CreateRelationship
    extends Command<RelationshipId>
{
    private QualifiedRelationshipTypeName type;

    private ContentId fromContent;

    private ContentId toContent;

    private Map<String, String> properties = Maps.newLinkedHashMap();

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


    @Override
    public void validate()
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( fromContent, "fromContent cannot be null" );
        Preconditions.checkNotNull( toContent, "toContent cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final CreateRelationship that = (CreateRelationship) o;

        return Objects.equals( fromContent, that.fromContent ) &&
            Objects.equals( toContent, that.toContent ) &&
            Objects.equals( type, that.type ) &&
            Objects.equals( properties, that.properties );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fromContent, toContent, type, properties );
    }
}
