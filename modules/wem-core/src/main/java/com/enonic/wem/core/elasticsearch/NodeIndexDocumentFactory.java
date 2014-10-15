package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.document.StoreDocument;
import com.enonic.wem.core.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.repository.IndexNameResolver;

import static com.enonic.wem.core.entity.index.IndexPaths.CREATED_TIME_PROPERTY;
import static com.enonic.wem.core.entity.index.IndexPaths.CREATOR_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.MODIFIED_TIME_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.MODIFIER_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.NAME_PROPERTY;
import static com.enonic.wem.core.entity.index.IndexPaths.PARENT_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.PATH_PROPERTY_PATH;


class NodeIndexDocumentFactory
{

    public static Collection<StoreDocument> create( final Node node, final Workspace workspace, final RepositoryId repositoryId )
    {
        node.validateForIndexing();

        Set<StoreDocument> storeDocuments = Sets.newHashSet();

        storeDocuments.add( createDataDocument( node, workspace, repositoryId ) );

        return storeDocuments;
    }

    private static StoreDocument createDataDocument( final Node node, final Workspace workspace, final RepositoryId repositoryId )
    {
        final IndexConfigDocument indexConfigDocument = node.getIndexConfigDocument();

        final StoreDocument.Builder builder = StoreDocument.create().
            id( node.id() ).
            indexName( IndexNameResolver.resolveSearchIndexName( repositoryId ) ).
            indexTypeName( workspace.getName() ).
            analyzer( indexConfigDocument.getAnalyzer() );

        addNodeMetaData( node, builder );
        addNodeProperties( node, builder );

        return builder.build();
    }

    private static void addNodeMetaData( final Node node, final StoreDocument.Builder builder )
    {

        if ( node.name() != null )
        {
            final Value nameValue = Value.newString( node.name().toString() );
            builder.addEntries( StoreDocumentItemFactory.create( NAME_PROPERTY, nameValue, IndexConfig.FULLTEXT ) );
        }

        if ( node.getCreatedTime() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( CREATED_TIME_PROPERTY, Value.newInstant( node.getCreatedTime() ), IndexConfig.MINIMAL ) );
        }

        if ( node.getCreator() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( CREATOR_PROPERTY_PATH, Value.newString( node.getCreator().getQualifiedName() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getModifiedTime() != null )
        {
            builder.addEntries( StoreDocumentItemFactory.create( MODIFIED_TIME_PROPERTY_PATH, Value.newInstant( node.getModifiedTime() ),
                                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.getModifier() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( MODIFIER_PROPERTY_PATH, Value.newString( node.getModifier().getQualifiedName() ),
                                                 IndexConfig.MINIMAL ) );
        }

        if ( node.path() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( PATH_PROPERTY_PATH, Value.newString( node.path().toString() ), IndexConfig.MINIMAL ) );
        }

        if ( node.parent() != null )
        {
            builder.addEntries(
                StoreDocumentItemFactory.create( PARENT_PROPERTY_PATH, Value.newString( node.parent().toString() ), IndexConfig.MINIMAL ) );
        }

    }

    private static void addNodeProperties( final Node node, final StoreDocument.Builder builder )
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