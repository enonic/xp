package com.enonic.wem.api.node;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Node
    implements ChangeTraceable, IllegalEditAware<Node>
{
    private final NodeId id;

    private final NodeName name;

    private final NodePath parent;

    private final NodePath path;

    private final PrincipalKey modifier;

    private final PrincipalKey creator;

    private final boolean hasChildren;

    private final Instant createdTime;

    private final PropertyTree data;

    private final Instant modifiedTime;

    private final IndexConfigDocument indexConfigDocument;

    private final Attachments attachments;

    private final ChildOrder childOrder;

    private final Long manualOrderValue;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final NodeType nodeType;

    private Node( final BaseBuilder builder )
    {
        this.id = builder.id;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;

        this.data = builder.data != null ? builder.data : new PropertyTree();

        this.attachments = builder.attachments == null ? Attachments.empty() : builder.attachments;

        if ( builder.indexConfigDocument != null )
        {
            this.indexConfigDocument = builder.indexConfigDocument;
        }
        else
        {
            this.indexConfigDocument = PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.BY_TYPE ).
                build();
        }

        this.creator = builder.creator;
        this.name = builder.name;
        this.parent = builder.parent;
        this.path = this.parent != null && this.name != null ? new NodePath( this.parent, this.name ) : null;
        this.modifier = builder.modifier;
        this.hasChildren = builder.hasChildren;
        this.childOrder = builder.childOrder;
        this.manualOrderValue = builder.manualOrderValue;
        this.permissions = builder.permissions == null ? AccessControlList.empty() : builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.nodeType = builder.nodeType;
    }

    public NodeName name()
    {
        return name;
    }

    public NodePath parent()
    {
        return parent;
    }

    public NodePath path()
    {
        return path;
    }

    public PrincipalKey creator()
    {
        return creator;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey modifier()
    {
        return modifier;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public boolean getHasChildren()
    {
        return hasChildren;
    }

    public NodeId id()
    {
        return id;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public PropertyTree data()
    {
        return this.data;
    }

    public Attachments attachments()
    {
        return this.attachments;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean inheritsPermissions()
    {
        return inheritPermissions;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public void validateForIndexing()
    {
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.indexConfigDocument, "EntityIndexConfig must be set" );
    }

    @Override
    public void checkIllegalEdit( final Node to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.id(), to.id(), Node.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Node.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Node.class );

        IllegalEdit.check( "parent", this.parent(), to.parent(), Node.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Node.class );
        IllegalEdit.check( "modifier", this.modifier(), to.modifier(), Node.class );
    }

    public String toString()
    {
        return this.path.toString();
    }

    public static Builder newNode()
    {
        return new Builder();
    }

    public static Builder newNode( final NodeId id )
    {
        return new Builder( id );
    }

    public static Builder newNode( final Node node )
    {
        return new Builder( node );
    }

    public static EditBuilder editNode( final Node original )
    {
        return new EditBuilder( original );
    }

    private static class BaseBuilder
    {
        NodeName name;

        NodePath parent;

        PrincipalKey modifier;

        PrincipalKey creator;

        boolean hasChildren = false;

        NodeId id;

        Instant createdTime;

        Instant modifiedTime;

        PropertyTree data = new PropertyTree();

        Attachments attachments;

        IndexConfigDocument indexConfigDocument;

        ChildOrder childOrder;

        Long manualOrderValue;

        AccessControlList permissions;

        boolean inheritPermissions;

        NodeType nodeType;

        private BaseBuilder()
        {
        }

        private BaseBuilder( final Node node )
        {
            this.id = node.id;
            this.createdTime = node.createdTime;
            this.modifiedTime = node.modifiedTime;
            this.data = node.data;
            this.indexConfigDocument = node.indexConfigDocument;
            this.name = node.name;
            this.parent = node.parent;
            this.creator = node.creator;
            this.modifier = node.modifier;
            this.childOrder = node.childOrder;
            this.manualOrderValue = node.manualOrderValue;
            this.permissions = node.permissions;
            this.inheritPermissions = node.inheritPermissions;
            this.nodeType = node.nodeType;
            this.attachments = node.attachments;
        }

        private BaseBuilder( final NodeId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
            this.inheritPermissions = true;
        }
    }

    public static class Builder
        extends BaseBuilder
    {
        private NodeName name;

        private NodePath parent;

        private PrincipalKey modifier;

        private PrincipalKey creator;

        boolean hasChildren = false;

        private ChildOrder childOrder;

        private Long manualOrderValue;

        private AccessControlList permissions;

        private boolean inheritPermissions;

        private NodeType nodeType = NodeType.DEFAULT_NODE_COLLECTION;

        public Builder()
        {
            super();
        }

        public Builder( final NodeId id )
        {
            this.id = id;
        }

        public Builder( final Node node )
        {
            this.id = node.id;
            this.createdTime = node.createdTime;
            this.modifiedTime = node.modifiedTime;
            this.data = node.data;
            this.attachments = node.attachments;
            this.name = node.name;
            this.parent = node.parent;
            this.modifier = node.modifier;
            this.creator = node.creator;
            this.childOrder = node.childOrder;
            this.manualOrderValue = node.manualOrderValue;
            this.permissions = node.permissions;
            this.inheritPermissions = node.inheritPermissions;
            this.nodeType = node.nodeType;
        }

        public Builder( final NodeId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder name( final NodeName value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.parent = new NodePath( value );
            return this;
        }

        public Builder parent( final NodePath value )
        {
            this.parent = value;
            return this;
        }

        public Builder creator( final PrincipalKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifier( final PrincipalKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder createdTime( final Instant value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( final Instant value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder data( final PropertyTree value )
        {
            this.data = value;
            return this;
        }

        public Builder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder attachments( final Attachments value )
        {
            this.attachments = value;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder nodeType( final NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Node build()
        {
            BaseBuilder baseBuilder = new BaseBuilder();
            baseBuilder.id = this.id;
            baseBuilder.createdTime = this.createdTime;
            baseBuilder.modifiedTime = this.modifiedTime;
            baseBuilder.data = this.data;
            baseBuilder.attachments = this.attachments;
            baseBuilder.name = this.name;
            baseBuilder.parent = this.parent;
            baseBuilder.creator = this.creator;
            baseBuilder.modifier = this.modifier;
            baseBuilder.indexConfigDocument = this.indexConfigDocument;
            baseBuilder.hasChildren = this.hasChildren;
            baseBuilder.childOrder = this.childOrder;
            baseBuilder.manualOrderValue = this.manualOrderValue;
            baseBuilder.permissions = this.permissions;
            baseBuilder.inheritPermissions = this.inheritPermissions;
            baseBuilder.nodeType = this.nodeType;

            return new Node( baseBuilder );
        }
    }

    public static class EditBuilder
        extends BaseBuilder
    {
        private final Node originalNode;

        final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Node original )
        {
            super( original );
            this.originalNode = original;
        }

        public EditBuilder name( final NodeName value )
        {
            changes.recordChange( newPossibleChange( "name" ).from( this.originalNode.name ).to( value ).build() );
            this.name = value;
            return this;
        }

        public EditBuilder rootDataSet( final PropertyTree value )
        {
            this.data = value;
            changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            return this;
        }

        public EditBuilder attachments( final Attachments value )
        {
            this.attachments = value;
            changes.recordChange(
                newPossibleChange( "attachments" ).from( this.originalNode.attachments() ).to( this.attachments ).build() );
            return this;
        }

        public EditBuilder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            changes.recordChange(
                newPossibleChange( "data" ).from( this.originalNode.indexConfigDocument ).to( this.indexConfigDocument ).build() );
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public EditBuilder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            changes.recordChange( newPossibleChange( "childOrder" ).from( this.originalNode.childOrder ).to( this.childOrder ).build() );
            return this;
        }

        public EditBuilder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            changes.recordChange(
                newPossibleChange( "manualOrderValue" ).from( this.originalNode.manualOrderValue ).to( this.manualOrderValue ).build() );
            return this;
        }

        public EditBuilder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            changes.recordChange( newPossibleChange( "permissions" ).from( this.originalNode.permissions ).to( this.permissions ).build() );
            return this;
        }

        public EditBuilder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            changes.recordChange( newPossibleChange( "inheritPermissions" ).from( this.originalNode.inheritPermissions ).to(
                this.inheritPermissions ).build() );
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }

        public Node build()
        {
            return new Node( this );
        }
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

        final Node node = (Node) o;

        if ( hasChildren != node.hasChildren )
        {
            return false;
        }
        if ( inheritPermissions != node.inheritPermissions )
        {
            return false;
        }
        if ( permissions != null ? !permissions.equals( node.permissions ) : node.permissions != null )
        {
            return false;
        }
        if ( attachments != null ? !attachments.equals( node.attachments ) : node.attachments != null )
        {
            return false;
        }
        if ( childOrder != null ? !childOrder.equals( node.childOrder ) : node.childOrder != null )
        {
            return false;
        }
        if ( nodeType != null ? !nodeType.equals( node.nodeType ) : node.nodeType != null )
        {
            return false;
        }
        if ( createdTime != null ? !createdTime.equals( node.createdTime ) : node.createdTime != null )
        {
            return false;
        }
        if ( creator != null ? !creator.equals( node.creator ) : node.creator != null )
        {
            return false;
        }
        if ( data != null ? !data.equals( node.data ) : node.data != null )
        {
            return false;
        }
        if ( id != null ? !id.equals( node.id ) : node.id != null )
        {
            return false;
        }
        if ( indexConfigDocument != null ? !indexConfigDocument.equals( node.indexConfigDocument ) : node.indexConfigDocument != null )
        {
            return false;
        }
        if ( manualOrderValue != null ? !manualOrderValue.equals( node.manualOrderValue ) : node.manualOrderValue != null )
        {
            return false;
        }
        if ( modifiedTime != null ? !modifiedTime.equals( node.modifiedTime ) : node.modifiedTime != null )
        {
            return false;
        }
        if ( modifier != null ? !modifier.equals( node.modifier ) : node.modifier != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( node.name ) : node.name != null )
        {
            return false;
        }
        if ( parent != null ? !parent.equals( node.parent ) : node.parent != null )
        {
            return false;
        }
        if ( path != null ? !path.equals( node.path ) : node.path != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = 31 * result + ( modifier != null ? modifier.hashCode() : 0 );
        result = 31 * result + ( creator != null ? creator.hashCode() : 0 );
        result = 31 * result + ( hasChildren ? 1 : 0 );
        result = 31 * result + ( createdTime != null ? createdTime.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( modifiedTime != null ? modifiedTime.hashCode() : 0 );
        result = 31 * result + ( indexConfigDocument != null ? indexConfigDocument.hashCode() : 0 );
        result = 31 * result + ( attachments != null ? attachments.hashCode() : 0 );
        result = 31 * result + ( childOrder != null ? childOrder.hashCode() : 0 );
        result = 31 * result + ( manualOrderValue != null ? manualOrderValue.hashCode() : 0 );
        result = 31 * result + ( permissions != null ? permissions.hashCode() : 0 );
        result = 31 * result + ( inheritPermissions ? 1 : 0 );
        result = 31 * result + ( nodeType != null ? nodeType.hashCode() : 0 );
        return result;
    }
}
