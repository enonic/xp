package com.enonic.wem.api.item;


import java.util.Objects;

import com.enonic.wem.api.data.DataPath;

/**
 * The natural key of a Relationship.
 */
public final class RelationshipKey
{
    private final QualifiedRelationshipTypeName type;

    private final ItemId fromItem;

    private final ItemId toItem;

    private final DataPath managingData;

    private RelationshipKey( final QualifiedRelationshipTypeName type, final ItemId fromContent, final ItemId toItem,
                             final DataPath managingData )
    {
        this.type = type;
        this.fromItem = fromContent;
        this.toItem = toItem;
        this.managingData = managingData;
    }

    private RelationshipKey( final Builder builder )
    {
        this.type = builder.type;
        this.fromItem = builder.fromItem;
        this.toItem = builder.toItem;
        this.managingData = builder.managingData;
    }

    public QualifiedRelationshipTypeName getType()
    {
        return type;
    }

    public ItemId getFromItem()
    {
        return fromItem;
    }

    public DataPath getManagingData()
    {
        return managingData;
    }

    public ItemId getToItem()
    {
        return toItem;
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

        final RelationshipKey that = (RelationshipKey) o;

        return Objects.equals( type, that.type ) &&
            Objects.equals( fromItem, that.fromItem ) &&
            Objects.equals( toItem, that.toItem ) &&
            Objects.equals( managingData, that.managingData );
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).
            add( "fromItem", fromItem ).
            add( "toItem", toItem ).
            add( "type", type ).
            add( "managingData", managingData ).
            toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, fromItem, toItem, managingData );
    }

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ItemId fromItem, final ItemId toItem )
    {
        return new RelationshipKey( type, fromItem, toItem, null );
    }

    public static RelationshipKey from( final QualifiedRelationshipTypeName type, final ItemId fromItem, final DataPath managingData,
                                        final ItemId toItem )
    {
        return new RelationshipKey( type, fromItem, toItem, managingData );
    }

    public static Builder newRelationshipKey()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QualifiedRelationshipTypeName type;

        private ItemId fromItem;

        private ItemId toItem;

        private DataPath managingData;

        public Builder type( QualifiedRelationshipTypeName relationshipType )
        {
            this.type = relationshipType;
            return this;
        }

        public Builder fromItem( ItemId contentId )
        {
            this.fromItem = contentId;
            return this;
        }

        public Builder toItem( ItemId contentId )
        {
            this.toItem = contentId;
            return this;
        }

        public Builder managingData( DataPath dataPath )
        {
            this.managingData = dataPath;
            return this;
        }

        public RelationshipKey build()
        {
            return new RelationshipKey( this );
        }
    }
}
