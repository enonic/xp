package com.enonic.xp.repo.impl.elasticsearch;

import java.time.Instant;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItemFactory;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;
import com.enonic.xp.security.acl.AccessControlList;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;


public class NodeStoreDocumentFactory
{
    private final NodeId nodeId;

    private final IndexConfigDocument indexConfigDocument;

    private final NodeType nodeType;

    private final AccessControlList permissions;

    private final PropertyTree data;

    private final Long manualOrderValue;

    private final NodePath nodePath;

    private final NodeVersionId versionId;

    private final Instant timestamp;

    private NodeStoreDocumentFactory( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.indexConfigDocument = builder.indexConfigDocument;
        this.nodeType = requireNonNullElse( builder.nodeType, NodeType.DEFAULT_NODE_COLLECTION );
        this.permissions = requireNonNullElse( builder.permissions, AccessControlList.empty() );
        this.data = requireNonNullElseGet( builder.data, PropertyTree::new );
        this.manualOrderValue = builder.manualOrderValue;
        this.nodePath = builder.nodePath;
        this.versionId = builder.versionId;
        this.timestamp = builder.timestamp;
    }

    public static Builder createBuilder()
    {
        return new Builder();
    }

    public static IndexDocument from( final NodeStoreVersion nodeStoreVersion, final NodeBranchEntry nodeBranchEntry )
    {
        return NodeStoreDocumentFactory.createBuilder()
            .nodeId( nodeStoreVersion.id() )
            .indexConfigDocument( nodeStoreVersion.indexConfigDocument() )
            .nodeType( nodeStoreVersion.nodeType() )
            .permissions( nodeStoreVersion.permissions() )
            .data( nodeStoreVersion.data() )
            .manualOrderValue( nodeStoreVersion.manualOrderValue() )
            .nodePath( nodeBranchEntry.getNodePath() )
            .versionId( nodeBranchEntry.getVersionId() )
            .timestamp( nodeBranchEntry.getTimestamp() )
            .build()
            .create();
    }

    public IndexDocument create()
    {
        return new IndexDocument( this.nodeId.toString(), createIndexItems(), this.indexConfigDocument.getAnalyzer() );
    }

    private IndexItems createIndexItems()
    {
        final IndexItems.Builder builder = IndexItems.create();

        addNodeMetaData( builder );
        addNodeDataProperties( builder );

        return builder.build();
    }

    private void addNodeMetaData( final IndexItems.Builder builder )
    {
        builder.add( IndexItemFactory.create( NodeIndexPath.VERSION, ValueFactory.newString( this.versionId.toString() ),
                                              createDefaultDocument( IndexConfig.MINIMAL ) ) );
        builder.add( IndexItemFactory.create( NodeIndexPath.TIMESTAMP, ValueFactory.newDateTime( this.timestamp ),
                                              createDefaultDocument( IndexConfig.MINIMAL ) ) );

        builder.add( IndexItemFactory.create( NodeIndexPath.PATH, ValueFactory.newString( this.nodePath.toString() ),
                                              createDefaultDocument( IndexConfig.PATH ) ) );
        if ( !this.nodePath.isRoot() )
        {
            builder.add(
                IndexItemFactory.create( NodeIndexPath.PARENT_PATH, ValueFactory.newString( this.nodePath.getParentPath().toString() ),
                                         createDefaultDocument( IndexConfig.MINIMAL ) ) );
            builder.add( IndexItemFactory.create( NodeIndexPath.NAME, ValueFactory.newString( this.nodePath.getName().toString() ),
                                                  createDefaultDocument( IndexConfig.FULLTEXT ) ) );
        }

        if ( this.manualOrderValue != null )
        {
            builder.add( IndexItemFactory.create( NodeIndexPath.MANUAL_ORDER_VALUE, ValueFactory.newLong( this.manualOrderValue ),
                                                  createDefaultDocument( IndexConfig.MINIMAL ) ) );
        }
        builder.add( IndexItemFactory.create( NodeIndexPath.NODE_TYPE, ValueFactory.newString( this.nodeType.toString() ),
                                              createDefaultDocument( IndexConfig.MINIMAL ) ) );
        builder.add( AccessControlListStoreDocumentFactory.create( this.permissions ) );
    }

    private void addNodeDataProperties( final IndexItems.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( !isNullOrEmpty( property.getString() ) )
                {
                    final IndexPath indexPath = IndexPath.from( property.getPath() );
                    final IndexConfig configForData = indexConfigDocument.getConfigForPath( indexPath );

                    if ( configForData == null )
                    {
                        throw new RuntimeException( "Missing index configuration for data " + property.getPath() );
                    }

                    builder.add( IndexItemFactory.create( indexPath, property.getValue(), indexConfigDocument ) );

                    if ( property.getType().equals( ValueTypes.REFERENCE ) )
                    {
                        builder.add( IndexItemFactory.create( NodeIndexPath.REFERENCE, property.getValue(),
                                                              createDefaultDocument( IndexConfig.MINIMAL ) ) );
                    }
                }
            }

        };

        visitor.traverse( this.data );
    }

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        return PatternIndexConfigDocument.create()
            .defaultConfig( indexConfig )
            .allTextConfig( this.indexConfigDocument.getAllTextConfig() )
            .build();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private IndexConfigDocument indexConfigDocument;

        private NodeType nodeType;

        private AccessControlList permissions;

        private PropertyTree data;

        private Long manualOrderValue;

        private NodePath nodePath;

        private NodeVersionId versionId;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder indexConfigDocument( IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder nodeType( NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder permissions( AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder data( PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder manualOrderValue( Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder versionId( NodeVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public NodeStoreDocumentFactory build()
        {
            return new NodeStoreDocumentFactory( this );
        }
    }
}
