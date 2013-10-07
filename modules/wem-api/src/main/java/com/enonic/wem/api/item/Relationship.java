package com.enonic.wem.api.item;


import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Relationship
    implements IllegalEditAware<Relationship>, ChangeTraceable
{
    private final RelationshipId id;

    private final DateTime createdTime;

    private final UserKey creator;

    private final DateTime modifiedTime;

    private final UserKey modifier;

    private final QualifiedRelationshipTypeName type;

    private final ItemId fromItem;

    private final ItemId toItem;

    private final ImmutableMap<String, String> properties;

    /**
     * Path to the Data in the fromItem that is managing this Relationship.
     */
    private final DataPath managingData;

    public Relationship( final Builder builder )
    {
        this.id = builder.id;
        this.type = builder.type;
        this.fromItem = builder.fromContent;
        this.toItem = builder.toContent;
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.properties = ImmutableMap.copyOf( builder.properties );
        this.managingData = builder.managingData;
    }

    public RelationshipId getId()
    {
        return id;
    }

    public RelationshipKey getKey()
    {
        return RelationshipKey.from( type, fromItem, managingData, toItem );
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public QualifiedRelationshipTypeName getType()
    {
        return type;
    }

    public ItemId getFromItem()
    {
        return fromItem;
    }

    public ItemId getToItem()
    {
        return toItem;
    }

    public ImmutableMap<String, String> getProperties()
    {
        return properties;
    }

    public String getProperty( String name )
    {
        return properties.get( name );
    }

    public boolean isManaged()
    {
        return managingData != null;
    }

    public DataPath getManagingData()
    {
        return managingData;
    }

    @Override
    public void checkIllegalEdit( final Relationship to )
        throws IllegalEditException
    {
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Relationship.class );
        IllegalEdit.check( "creator", this.getCreator(), to.getCreator(), Relationship.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Relationship.class );
        IllegalEdit.check( "modifier", this.getModifier(), to.getModifier(), Relationship.class );
        IllegalEdit.check( "fromItem", this.getFromItem(), to.getFromItem(), Relationship.class );
        IllegalEdit.check( "toItem", this.getToItem(), to.getToItem(), Relationship.class );
        IllegalEdit.check( "type", this.getType(), to.getType(), Relationship.class );
    }

    public static Builder newRelationship()
    {
        return new Builder();
    }

    public static Builder newRelationship( final Relationship relationship )
    {
        return new Builder( relationship );
    }

    public static class Builder
    {
        private RelationshipId id;

        private UserKey creator;

        private DateTime createdTime;

        private QualifiedRelationshipTypeName type;

        private ItemId fromContent;

        private ItemId toContent;

        private Map<String, String> properties = Maps.newLinkedHashMap();

        private DataPath managingData;

        private UserKey modifier;

        private DateTime modifiedTime;

        private Builder( final Relationship relationship )
        {
            id = relationship.id;
            creator = relationship.creator;
            createdTime = relationship.createdTime;
            modifier = relationship.modifier;
            modifiedTime = relationship.modifiedTime;
            type = relationship.type;
            fromContent = relationship.fromItem;
            toContent = relationship.toItem;
            properties.putAll( relationship.getProperties() );
            managingData = relationship.managingData;
        }

        private Builder()
        {
            // default
        }

        public Builder id( RelationshipId value )
        {
            this.id = value;
            return this;
        }

        public Builder type( QualifiedRelationshipTypeName value )
        {
            this.type = value;
            return this;
        }

        public Builder fromContent( ItemId value )
        {
            this.fromContent = value;
            return this;
        }

        public Builder toContent( ItemId value )
        {
            this.toContent = value;
            return this;
        }

        public Builder createdTime( DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder creator( UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifiedTime( DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder modifier( UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder properties( Map<String, String> properties )
        {
            Preconditions.checkNotNull( properties, "properties cannot be null" );
            this.properties = properties;
            return this;
        }

        public Builder property( String key, String value )
        {
            this.properties.put( key, value );
            return this;
        }

        public Builder managed( DataPath managingData )
        {
            this.managingData = managingData;
            return this;
        }

        public Relationship build()
        {
            return new Relationship( this );
        }

    }
}
