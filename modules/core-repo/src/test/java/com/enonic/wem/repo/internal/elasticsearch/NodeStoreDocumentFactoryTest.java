package com.enonic.wem.repo.internal.elasticsearch;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.xp.data.CounterPropertyIdProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

import static com.enonic.wem.repo.internal.TestContext.TEST_BRANCH;
import static com.enonic.wem.repo.internal.TestContext.TEST_REPOSITORY;
import static org.junit.Assert.*;

public class NodeStoreDocumentFactoryTest
{

    @Test
    public void validate_given_no_id_then_exception()
        throws Exception
    {
        Node node = Node.create().
            build();

        try
        {
            NodeStoreDocumentFactory.createBuilder().
                node( node ).
                nodeVersionId( NodeVersionId.from( "test" ) ).
                branch( TEST_BRANCH ).
                repositoryId( TEST_REPOSITORY.getId() ).
                build().
                create();
        }
        catch ( NullPointerException e )
        {
            assertEquals( "Id must be set", e.getMessage() );
            return;
        }

        fail( "Expected exception" );
    }

    @Test
    public void validate_given_id_then_ok()
    {
        Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( storeDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( storeDocuments );
        assertNotNull( getIndexDocumentOfType( storeDocuments, "test" ) );
    }

    @Test
    public void set_analyzer()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        final Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertEquals( myAnalyzerName, storeDocument.getAnalyzer() );
    }

    @Test
    public void node_index_document_meta_data_values()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            parentPath( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertEquals( myAnalyzerName, storeDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TEST_REPOSITORY.getId() ), storeDocument.getIndexName() );
        assertEquals( "test", storeDocument.getIndexTypeName() );
    }

    @Test
    public void add_properties_then_index_document_items_created_for_each_property()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree( new CounterPropertyIdProvider() );
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "a.b.d", LocalDate.now() );

        Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            data( rootDataSet ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final StoreDocument storeDocument = getIndexDocumentOfType( storeDocuments, "test" );

        assertNotNull( getItemWithName( storeDocument, IndexPath.from( "a.b.c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( storeDocument, IndexPath.from( "a.b.d" ), IndexValueType.DATETIME ) );
    }

    @Test
    public void create_for_properties_with_multiple_values()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setDouble( "a.b.c[1]", 3.0 );

        Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            data( rootDataSet ).
            build();

        final Collection<StoreDocument> storeDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        assertTrue( storeDocuments.iterator().hasNext() );
        final StoreDocument next = storeDocuments.iterator().next();

        final Set<AbstractStoreDocumentItem> numberItems = getItemsWithName( next, IndexPath.from( "a.b.c" ), IndexValueType.NUMBER );

        assertEquals( 2, numberItems.size() );

    }

    private StoreDocument getIndexDocumentOfType( final Collection<StoreDocument> storeDocuments, final String indexType )
    {
        for ( StoreDocument storeDocument : storeDocuments )
        {
            if ( indexType.equals( storeDocument.getIndexTypeName() ) )
            {
                return storeDocument;
            }
        }
        return null;
    }

    AbstractStoreDocumentItem getItemWithName( final StoreDocument storeDocument, final IndexPath indexPath, final IndexValueType baseType )
    {
        for ( AbstractStoreDocumentItem item : storeDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                return item;
            }
        }

        return null;
    }

    Set<AbstractStoreDocumentItem> getItemsWithName( final StoreDocument storeDocument, final IndexPath indexPath,
                                                     final IndexValueType baseType )
    {
        Set<AbstractStoreDocumentItem> items = Sets.newHashSet();

        for ( AbstractStoreDocumentItem item : storeDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                items.add( item );
            }
        }

        return items;
    }


}
