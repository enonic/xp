package com.enonic.wem.core.entity.index;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentItemFactory;
import com.enonic.wem.core.repository.IndexNameResolver;

import static com.enonic.wem.core.entity.index.IndexPaths.CREATED_TIME_PROPERTY;
import static com.enonic.wem.core.entity.index.IndexPaths.CREATOR_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.MODIFIED_TIME_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.MODIFIER_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.NAME_PROPERTY;
import static com.enonic.wem.core.entity.index.IndexPaths.PARENT_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.PATH_PROPERTY_PATH;


public class NodeIndexDocumentFactory
{

    private static final PropertyIndexConfig metadataPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        nGramEnabled( false ).
        fulltextEnabled( false ).
        build();

    private static final PropertyIndexConfig defaultPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        nGramEnabled( false ).
        fulltextEnabled( false ).
        build();

    private static final PropertyIndexConfig namePropertyIndexConfig = PropertyIndexConfig.
        newPropertyIndexConfig().
        enabled( true ).
        nGramEnabled( true ).
        fulltextEnabled( false ).
        build();

    public static Collection<IndexDocument> create( final Node node, final Workspace workspace, final Repository repository )
    {
        node.validateForIndexing();

        Set<IndexDocument> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createDataDocument( node, workspace, repository ) );

        return indexDocuments;
    }

    private static IndexDocument createDataDocument( final Node node, final Workspace workspace, final Repository repository )
    {
        final EntityIndexConfig entityIndexConfig = node.getEntityIndexConfig();

        final IndexDocument.Builder builder = IndexDocument.newIndexDocument().
            id( node.id() ).
            index( IndexNameResolver.resolveSearchIndexName( repository ) ).
            indexType( workspace.getName() ).
            analyzer( entityIndexConfig.getAnalyzer() ).
            collection( entityIndexConfig.getCollection() );

        final IndexDocumentItemFactory indexDocumentItemFactory = new IndexDocumentItemFactory( true );

        addNodeMetaData( node, builder, indexDocumentItemFactory );
        addNodeProperties( node, builder, indexDocumentItemFactory );

        return builder.build();
    }

    private static void addNodeMetaData( final Node node, final IndexDocument.Builder builder, final IndexDocumentItemFactory factory )
    {

        if ( node.name() != null )
        {
            final Value nameValue = Value.newString( node.name().toString() );
            builder.addEntries( factory.create( NAME_PROPERTY, nameValue, namePropertyIndexConfig ) );
        }

        if ( node.getCreatedTime() != null )
        {
            builder.addEntries(
                factory.create( CREATED_TIME_PROPERTY, Value.newInstant( node.getCreatedTime() ), metadataPropertyIndexConfig ) );
        }

        if ( node.getCreator() != null )
        {
            builder.addEntries( factory.create( CREATOR_PROPERTY_PATH, Value.newString( node.getCreator().getQualifiedName() ),
                                                metadataPropertyIndexConfig ) );
        }

        if ( node.getModifiedTime() != null )
        {
            builder.addEntries(
                factory.create( MODIFIED_TIME_PROPERTY_PATH, Value.newInstant( node.getModifiedTime() ), metadataPropertyIndexConfig ) );
        }

        if ( node.getModifier() != null )
        {
            builder.addEntries( factory.create( MODIFIER_PROPERTY_PATH, Value.newString( node.getModifier().getQualifiedName() ),
                                                metadataPropertyIndexConfig ) );
        }

        if ( node.path() != null )
        {
            builder.addEntries(
                factory.create( PATH_PROPERTY_PATH, Value.newString( node.path().toString() ), metadataPropertyIndexConfig ) );
        }

        if ( node.parent() != null )
        {
            builder.addEntries(
                factory.create( PARENT_PROPERTY_PATH, Value.newString( node.parent().toString() ), metadataPropertyIndexConfig ) );
        }

    }

    private static void addNodeProperties( final Node node, final IndexDocument.Builder builder, final IndexDocumentItemFactory factory )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( !property.hasNullValue() && !Strings.isNullOrEmpty( property.getValue().asString() ) )
                {
                    PropertyIndexConfig propertyIndexConfig = node.getEntityIndexConfig().getPropertyIndexConfig( property.getPath() );

                    if ( propertyIndexConfig == null )
                    {
                        propertyIndexConfig = defaultPropertyIndexConfig;
                    }

                    builder.addEntries( factory.create( property, propertyIndexConfig ) );
                }
            }
        };

        visitor.traverse( node.data() );
    }


}