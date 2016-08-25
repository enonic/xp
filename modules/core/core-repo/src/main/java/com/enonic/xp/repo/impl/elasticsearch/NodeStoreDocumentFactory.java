package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.RepositoryId;


public class NodeStoreDocumentFactory
{
    private final Node node;

    private final Branch branch;

    private final RepositoryId repositoryId;

    private final boolean refresh;

    private NodeStoreDocumentFactory( final Builder builder )
    {
        node = builder.node;
        branch = builder.branch;
        repositoryId = builder.repositoryId;
        this.refresh = builder.refresh;
    }

    public static Builder createBuilder()
    {
        return new Builder();
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
            id( this.node.id().toString() ).
            indexName( IndexNameResolver.resolveSearchIndexName( this.repositoryId ) ).
            indexTypeName( this.branch.getValue() ).
            analyzer( indexConfigDocument.getAnalyzer() ).
            indexItems( createIndexItems() ).
            refreshAfterOperation( this.refresh );

        return builder.build();
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
        addNodeBaseProperties( builder );

        builder.add( AccessControlListStoreDocumentFactory.create( this.node.getPermissions() ) );
    }

    private void addNodeBaseProperties( final IndexItems.Builder builder )
    {
        addNodeVersion( builder );

        addNodeName( builder );

        addNodePath( builder );

        addParentPath( builder );

        addManualOrderValue( builder );

        addNodeType( builder );

        addTimestamp( builder );

        addNodeState( builder );
    }

    private void addNodeState( final IndexItems.Builder builder )
    {
        if ( this.node.getNodeState() != null )
        {
            builder.add( NodeIndexPath.STATE, ValueFactory.newString( this.node.getNodeState().value() ), IndexConfig.MINIMAL );
        }
    }

    private void addTimestamp( final IndexItems.Builder builder )
    {
        if ( this.node.getTimestamp() != null )
        {
            builder.add( NodeIndexPath.TIMESTAMP, ValueFactory.newDateTime( this.node.getTimestamp() ), IndexConfig.MINIMAL );
        }
    }

    private void addNodeType( final IndexItems.Builder builder )
    {
        if ( this.node.getNodeType() != null )
        {
            builder.add( NodeIndexPath.NODE_TYPE, ValueFactory.newString( this.node.getNodeType().getName() ), IndexConfig.MINIMAL );
        }
    }

    private void addManualOrderValue( final IndexItems.Builder builder )
    {
        if ( this.node.getManualOrderValue() != null )
        {
            builder.add( NodeIndexPath.MANUAL_ORDER_VALUE, ValueFactory.newLong( this.node.getManualOrderValue() ), IndexConfig.MINIMAL );
        }
    }

    private void addParentPath( final IndexItems.Builder builder )
    {
        if ( this.node.parentPath() != null )
        {
            builder.add( NodeIndexPath.PARENT_PATH, ValueFactory.newString( this.node.parentPath().toString() ), IndexConfig.MINIMAL );
        }
    }

    private void addNodePath( final IndexItems.Builder builder )
    {
        if ( this.node.path() != null )
        {
            builder.add( NodeIndexPath.PATH, ValueFactory.newString( this.node.path().toString() ), IndexConfig.PATH );
        }
    }

    private void addNodeName( final IndexItems.Builder builder )
    {
        if ( this.node.name() != null )
        {
            builder.add( NodeIndexPath.NAME, ValueFactory.newString( this.node.name().toString() ), IndexConfig.FULLTEXT );
        }
    }

    private void addNodeVersion( final IndexItems.Builder builder )
    {
        if ( this.node.getNodeVersionId() != null )
        {
            builder.add( NodeIndexPath.VERSION, ValueFactory.newString( node.getNodeVersionId().toString() ), IndexConfig.MINIMAL );
        }
    }

    private void addNodeDataProperties( final IndexItems.Builder builder )
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

                    builder.add( property, configForData );

                    addReferenceAggregation( property );
                }
            }

            private void addReferenceAggregation( final Property property )
            {
                if ( property.getType().equals( ValueTypes.REFERENCE ) )
                {
                    builder.add( NodeIndexPath.REFERENCE, property.getValue(), IndexConfig.MINIMAL );
                }
            }
        };

        visitor.traverse( this.node.data() );
    }

    public static final class Builder
    {
        private Node node;

        private Branch branch;

        private RepositoryId repositoryId;

        private boolean refresh = false;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
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

        public NodeStoreDocumentFactory build()
        {
            return new NodeStoreDocumentFactory( this );
        }
    }
}