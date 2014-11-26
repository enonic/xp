package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;


class NodeStoreDocumentFactory
{

    public static Collection<StoreDocument> create( final Node node, final NodeVersionId nodeVersionId, final Workspace workspace,
                                                    final RepositoryId repositoryId )
    {
        node.validateForIndexing();

        Set<StoreDocument> storeDocuments = Sets.newHashSet();

        storeDocuments.add( createDataDocument( node, nodeVersionId, workspace, repositoryId ) );

        return storeDocuments;
    }

    private static StoreDocument createDataDocument( final Node node, final NodeVersionId nodeVersionId, final Workspace workspace,
                                                     final RepositoryId repositoryId )
    {
        final IndexConfigDocument indexConfigDocument = node.getIndexConfigDocument();

        final StoreDocument.Builder builder = StoreDocument.create().
            id( node.id() ).
            indexName( IndexNameResolver.resolveSearchIndexName( repositoryId ) ).
            indexTypeName( workspace.getName() ).
            analyzer( indexConfigDocument.getAnalyzer() );

        addNodeMetaData( node, nodeVersionId, builder );
        addNodeDataProperties( node, builder );

        return builder.build();
    }

    private static void addNodeMetaData( final Node node, final NodeVersionId nodeVersionId, final StoreDocument.Builder builder )
    {
        addNodeBaseProperties( node, nodeVersionId, builder );

        builder.addEntries( AccessControlListStoreDocumentFactory.create( node.getEffectiveAccessControlList() ) );
    }

    private static void addNodeBaseProperties( final Node node, final NodeVersionId nodeVersionId, final StoreDocument.Builder builder )
    {
        if ( nodeVersionId != null )
        {
            final Value nodeVersionIdValue = Value.newString( nodeVersionId.toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.VERSION, nodeVersionIdValue, IndexConfig.MINIMAL ) );
        }

        if ( node.name() != null )
        {
            final Value nameValue = Value.newString( node.name().toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.NAME, nameValue, IndexConfig.FULLTEXT ) );
        }

        if ( node.getCreatedTime() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.CREATED_TIME, Value.newInstant( node.getCreatedTime() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getCreator() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.CREATOR, Value.newString( node.getCreator().toString() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getModifiedTime() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.MODIFIED_TIME, Value.newInstant( node.getModifiedTime() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getModifier() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.MODIFIER, Value.newString( node.getModifier().toString() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.path() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.PATH, Value.newString( node.path().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.parent() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( NodeIndexPath.PARENT_PATH, Value.newString( node.parent().toString() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getManualOrderValue() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPath.MANUAL_ORDER_VALUE, Value.newLong( node.getManualOrderValue() ),
                                                 IndexConfig.MINIMAL ) );
        }
    }

    private static void addNodeDataProperties( final Node node, final StoreDocument.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( !property.hasNullValue() && !Strings.isNullOrEmpty( property.getValue().asString() ) )
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

        visitor.traverse( node.data() );
    }


}