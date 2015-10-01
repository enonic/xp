package com.enonic.xp.repo.impl.elasticsearch;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.TestContext;
import com.enonic.xp.repo.impl.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;

import static org.junit.Assert.*;

public class NodeIndexDocumentFactoryTest
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
                branch( TestContext.TEST_BRANCH ).
                repositoryId( TestContext.TEST_REPOSITORY.getId() ).
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

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( indexDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        assertNotNull( indexDocuments );
        assertNotNull( getIndexDocumentOfType( indexDocuments, "test" ) );
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

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
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

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TestContext.TEST_REPOSITORY.getId() ), indexDocument.getIndexName() );
        assertEquals( "test", indexDocument.getIndexTypeName() );
    }

    @Test
    public void add_properties_then_index_document_items_created_for_each_property()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "a.b.d", LocalDate.now() );

        Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            data( rootDataSet ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertNotNull( getItemWithName( indexDocument, IndexPath.from( "a.b.c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( indexDocument, IndexPath.from( "a.b.d" ), IndexValueType.DATETIME ) );
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

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            nodeVersionId( NodeVersionId.from( "test" ) ).
            branch( TestContext.TEST_BRANCH ).
            repositoryId( TestContext.TEST_REPOSITORY.getId() ).
            build().
            create();

        assertTrue( indexDocuments.iterator().hasNext() );
        final IndexDocument next = indexDocuments.iterator().next();

        final Set<AbstractStoreDocumentItem> numberItems = getItemsWithName( next, IndexPath.from( "a.b.c" ), IndexValueType.NUMBER );

        assertEquals( 2, numberItems.size() );

    }

    private IndexDocument getIndexDocumentOfType( final Collection<IndexDocument> indexDocuments, final String indexType )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            if ( indexType.equals( indexDocument.getIndexTypeName() ) )
            {
                return indexDocument;
            }
        }
        return null;
    }

    AbstractStoreDocumentItem getItemWithName( final IndexDocument indexDocument, final IndexPath indexPath, final IndexValueType baseType )
    {
        for ( AbstractStoreDocumentItem item : indexDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                return item;
            }
        }

        return null;
    }

    Set<AbstractStoreDocumentItem> getItemsWithName( final IndexDocument indexDocument, final IndexPath indexPath,
                                                     final IndexValueType baseType )
    {
        Set<AbstractStoreDocumentItem> items = Sets.newHashSet();

        for ( AbstractStoreDocumentItem item : indexDocument.getStoreDocumentItems() )
        {
            if ( item.getPath().equals( indexPath.getPath() ) && item.getIndexBaseType().equals( baseType ) )
            {
                items.add( item );
            }
        }

        return items;
    }


}
