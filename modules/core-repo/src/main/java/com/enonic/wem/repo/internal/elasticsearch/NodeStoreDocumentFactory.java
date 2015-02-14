package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.PropertyVisitor;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.index.IndexConfig;
import com.enonic.xp.core.index.IndexConfigDocument;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeIndexPath;
import com.enonic.xp.core.node.NodeState;
import com.enonic.xp.core.node.NodeVersionId;
import com.enonic.xp.core.repository.RepositoryId;
import com.enonic.xp.core.branch.Branch;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;


class NodeStoreDocumentFactory
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private final Branch branch;

    private final RepositoryId repositoryId;

    private final boolean refresh;

    private final NodeState state;

    private NodeStoreDocumentFactory( final Builder builder )
    {
        node = builder.node;
        nodeVersionId = builder.nodeVersionId;
        branch = builder.branch;
        repositoryId = builder.repositoryId;
        this.refresh = builder.refresh;
        this.state = builder.state;
    }

    public Collection<StoreDocument> create()
    {
        this.node.validateForIndexing();

        Set<StoreDocument> storeDocuments = Sets.newHashSet();

        storeDocuments.add( createDataDocument() );

        return storeDocuments;
    }

    private StoreDocument createDataDocument()
    {
        final IndexConfigDocument indexConfigDocument = this.node.getIndexConfigDocument();

        final StoreDocument.Builder builder = StoreDocument.create().
            id( this.node.id() ).
            indexName( IndexNameResolver.resolveSearchIndexName( this.repositoryId ) ).
            indexTypeName( this.branch.getName() ).
            analyzer( indexConfigDocument.getAnalyzer() ).
            refreshAfterOperation( this.refresh );

        addNodeMetaData( builder );
        addNodeDataProperties( builder );

        return builder.build();
    }

    private void addNodeMetaData( final StoreDocument.Builder builder )
    {
        addNodeBaseProperties( builder );

        builder.addEntries( AccessControlListStoreDocumentFactory.create( this.node.getPermissions() ) );
    }

    private void addNodeBaseProperties( final StoreDocument.Builder builder )
    {
        if ( this.nodeVersionId != null )
        {
            final Value nodeVersionIdValue = Value.newString( this.nodeVersionId.toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.VERSION, nodeVersionIdValue, IndexConfig.MINIMAL ) );
        }

        if ( this.node.name() != null )
        {
            final Value nameValue = Value.newString( this.node.name().toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.NAME, nameValue, IndexConfig.FULLTEXT ) );
        }

        if ( this.node.path() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.PATH, Value.newString( this.node.path().toString() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.parentPath() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.PARENT_PATH, Value.newString( this.node.parentPath().toString() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.getManualOrderValue() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.MANUAL_ORDER_VALUE, Value.newLong( this.node.getManualOrderValue() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.getNodeType() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.NODE_TYPE, Value.newString( this.node.getNodeType().getName() ),
                                                 IndexConfig.MINIMAL ) );
        }

        builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.STATE, Value.newString( this.node.getNodeState().value() ),
                                                             IndexConfig.MINIMAL ) );
    }

    private void addNodeDataProperties( final StoreDocument.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( !Strings.isNullOrEmpty( property.getString() ) )
                {
                    final IndexConfig configForData = node.getIndexConfigDocument().getConfigForPath( property.getPath() );

                    if ( configForData == null )
                    {
                        throw new RuntimeException( "Missing index configuration for data " + property.getPath() );
                    }

                    builder.addEntries( StoreDocumentItemFactory.create( property, configForData ) );
                }
            }
        };

        visitor.traverse( this.node.data() );
    }

    public static Builder createBuilder()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private NodeVersionId nodeVersionId;

        private Branch branch;

        private RepositoryId repositoryId;

        private boolean refresh = true;

        private NodeState state = NodeState.DEFAULT;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder branch( Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder state( final NodeState state )
        {
            this.state = state;
            return this;
        }

        public NodeStoreDocumentFactory build()
        {
            return new NodeStoreDocumentFactory( this );
        }
    }
}