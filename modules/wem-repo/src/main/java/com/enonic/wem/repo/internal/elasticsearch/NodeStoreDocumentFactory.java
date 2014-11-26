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
import com.enonic.wem.api.index.NodeIndexPaths;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

import static com.enonic.wem.api.index.NodeIndexPaths.CREATED_TIME_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.CREATOR_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.MANUAL_ORDER_VALUE_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.MODIFIED_TIME_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.MODIFIER_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.NAME_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.PARENT_PATH;
import static com.enonic.wem.api.index.NodeIndexPaths.PATH_PATH;


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
            builder.addEntries(
                StoreDocumentItemFactory.create( NodeIndexPaths.VERSION_KEY_PATH, nodeVersionIdValue, IndexConfig.MINIMAL ) );
        }

        if ( node.name() != null )
        {
            final Value nameValue = Value.newString( node.name().toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NAME_PATH, nameValue, IndexConfig.FULLTEXT ) );
        }

        if ( node.getCreatedTime() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( CREATED_TIME_PATH, Value.newInstant( node.getCreatedTime() ), IndexConfig.MINIMAL ) );
        }

        if ( node.getCreator() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( CREATOR_PATH, Value.newString( node.getCreator().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.getModifiedTime() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( MODIFIED_TIME_PATH, Value.newInstant( node.getModifiedTime() ), IndexConfig.MINIMAL ) );
        }

        if ( node.getModifier() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( MODIFIER_PATH, Value.newString( node.getModifier().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.path() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( PATH_PATH, Value.newString( node.path().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.parent() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( PARENT_PATH, Value.newString( node.parent().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.getManualOrderValue() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( MANUAL_ORDER_VALUE_PATH, Value.newLong( node.getManualOrderValue() ),
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