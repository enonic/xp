package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.elasticsearch.document.IndexDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;


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

    public Collection<IndexDocument> create()
    {
        this.node.validateForIndexing();

        Set<IndexDocument> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createDataDocument() );

        return indexDocuments;
    }

    private IndexDocument createDataDocument()
    {
        final IndexConfigDocument indexConfigDocument = this.node.getIndexConfigDocument();

        final IndexDocument.Builder builder = IndexDocument.create().
            id( this.node.id() ).
            indexName( IndexNameResolver.resolveSearchIndexName( this.repositoryId ) ).
            indexTypeName( this.branch.getName() ).
            analyzer( indexConfigDocument.getAnalyzer() ).
            refreshAfterOperation( this.refresh );

        addNodeMetaData( builder );
        addNodeDataProperties( builder );

        return builder.build();
    }

    private void addNodeMetaData( final IndexDocument.Builder builder )
    {
        addNodeBaseProperties( builder );

        builder.addEntries( AccessControlListStoreDocumentFactory.create( this.node.getPermissions() ) );
    }

    private void addNodeBaseProperties( final IndexDocument.Builder builder )
    {
        if ( this.nodeVersionId != null )
        {
            final Value nodeVersionIdValue = ValueFactory.newString( this.nodeVersionId.toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.VERSION, nodeVersionIdValue, IndexConfig.MINIMAL ) );
        }

        if ( this.node.name() != null )
        {
            final Value nameValue = ValueFactory.newString( this.node.name().toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.NAME, nameValue, IndexConfig.FULLTEXT ) );
        }

        if ( this.node.path() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.PATH, ValueFactory.newString( this.node.path().toString() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.parentPath() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.PARENT_PATH, ValueFactory.newString( this.node.parentPath().toString() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.getManualOrderValue() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.MANUAL_ORDER_VALUE, ValueFactory.newLong( this.node.getManualOrderValue() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( this.node.getNodeType() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.NODE_TYPE, ValueFactory.newString( this.node.getNodeType().getName() ),
                                                 IndexConfig.MINIMAL ) );
        }

        builder.addEntries(
            StoreDocumentItemFactory.create( NodeIndexPath.STATE, ValueFactory.newString( this.node.getNodeState().value() ),
                                             IndexConfig.MINIMAL ) );
    }

    private void addNodeDataProperties( final IndexDocument.Builder builder )
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

        private boolean refresh = false;

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