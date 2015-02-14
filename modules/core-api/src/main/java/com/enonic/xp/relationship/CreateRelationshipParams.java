package com.enonic.xp.relationship;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

public final class CreateRelationshipParams
{
    private RelationshipTypeName type;

    private ContentId fromContent;

    private ContentId toContent;

    private Map<String, String> properties = Maps.newLinkedHashMap();

    public RelationshipTypeName getType()
    {
        return type;
    }

    public CreateRelationshipParams type( final RelationshipTypeName type )
    {
        this.type = type;
        return this;
    }

    public ContentId getFromContent()
    {
        return fromContent;
    }

    public CreateRelationshipParams fromContent( final ContentId fromContent )
    {
        this.fromContent = fromContent;
        return this;
    }

    public ContentId getToContent()
    {
        return toContent;
    }

    public CreateRelationshipParams toContent( final ContentId toContent )
    {
        this.toContent = toContent;
        return this;
    }

    public CreateRelationshipParams property( final String key, final String value )
    {
        this.properties.put( key, value );
        return this;
    }

    public CreateRelationshipParams property( final Map<String, String> map )
    {
        for ( Map.Entry<String, String> entry : map.entrySet() )
        {
            final String key = entry.getKey();
            final String value = entry.getValue();
            property( key, value );
        }
        return this;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

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

        final CreateRelationshipParams that = (CreateRelationshipParams) o;

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
