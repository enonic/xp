package com.enonic.wem.core.item.index;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.EntityIndexConfig;
import com.enonic.wem.api.item.Node;
import com.enonic.wem.api.item.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.IndexDocument2;
import com.enonic.wem.core.index.indexdocument.IndexDocumentItemFactory;


public class NodeIndexDocumentFactory
{
    protected static final String CREATED_TIME_PROPERTY_NAME = "createdTime";

    protected static final String CREATOR_PROPERTY_NAME = "creator";

    protected static final String MODIFIED_TIME_PROPERTY_NAME = "modifiedTime";

    protected static final String MODIFIER_PROPERTY_NAME = "modifier";

    protected static final String PATH_PROPERTY_NAME = "path";

    private static final PropertyIndexConfig metadataPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        autocompleteEnabled( false ).
        fulltextEnabled( false ).
        build();

    private static final PropertyIndexConfig defaultPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        autocompleteEnabled( true ).
        fulltextEnabled( true ).
        build();

    public Collection<IndexDocument2> create( final Node node )
    {
        Set<IndexDocument2> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createDataDocument( node ) );

        return indexDocuments;
    }

    private IndexDocument2 createDataDocument( final Node node )
    {
        final EntityIndexConfig entityIndexConfig = node.getEntityIndexConfig();

        final IndexDocument2.Builder builder = IndexDocument2.newIndexDocument().
            id( node.id() ).
            index( IndexConstants.NODB_INDEX ).
            indexType( IndexType.NODE ).
            analyzer( entityIndexConfig.getAnalyzer() );

        addNodeMetaData( node, builder );
        addNodeProperties( node, builder );

        return builder.build();
    }

    private void addNodeMetaData( final Node node, final IndexDocument2.Builder builder )
    {
        // TODO: Add the rest of the metadata

        builder.addEntries( IndexDocumentItemFactory.create( CREATED_TIME_PROPERTY_NAME, new Value.DateTime( node.getCreatedTime() ),
                                                             metadataPropertyIndexConfig ) );

        builder.addEntries( IndexDocumentItemFactory.create( PATH_PROPERTY_NAME, new Value.String( node.path().toString() ),
                                                             metadataPropertyIndexConfig ) );

        if ( node.getCreator() != null )
        {
            builder.addEntries(
                IndexDocumentItemFactory.create( CREATOR_PROPERTY_NAME, new Value.String( node.getCreator().getQualifiedName() ),
                                                 metadataPropertyIndexConfig ) );
        }

        if ( node.getModifiedTime() != null )
        {
            builder.addEntries( IndexDocumentItemFactory.create( MODIFIED_TIME_PROPERTY_NAME, new Value.DateTime( node.getModifiedTime() ),
                                                                 metadataPropertyIndexConfig ) );
        }

        if ( node.getModifier() != null )
        {
            builder.addEntries(
                IndexDocumentItemFactory.create( MODIFIER_PROPERTY_NAME, new Value.String( node.getModifier().getQualifiedName() ),
                                                 metadataPropertyIndexConfig ) );
        }

    }

    private void addNodeProperties( final Node node, final IndexDocument2.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                PropertyIndexConfig propertyIndexConfig = node.getEntityIndexConfig().getPropertyIndexConfig( property.getPath() );

                if ( propertyIndexConfig == null )
                {
                    propertyIndexConfig = defaultPropertyIndexConfig;
                }

                builder.addEntries( IndexDocumentItemFactory.create( property, propertyIndexConfig ) );
            }
        };

        visitor.traverse( node.rootDataSet() );
    }


}