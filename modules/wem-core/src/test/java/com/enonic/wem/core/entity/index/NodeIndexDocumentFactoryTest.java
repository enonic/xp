package com.enonic.wem.core.entity.index;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.index.IndexValueType;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentItemPath;
import com.enonic.wem.core.repository.IndexNameResolver;

import static com.enonic.wem.core.TestContext.TEST_REPOSITORY;
import static com.enonic.wem.core.TestContext.TEST_WORKSPACE;
import static com.enonic.wem.core.entity.index.IndexPaths.CREATED_TIME_PROPERTY;
import static com.enonic.wem.core.entity.index.IndexPaths.CREATOR_PROPERTY_PATH;
import static com.enonic.wem.core.entity.index.IndexPaths.MODIFIER_PROPERTY_PATH;
import static org.junit.Assert.*;

public class NodeIndexDocumentFactoryTest
{

    @Test
    public void validate_given_no_id_then_exception()
        throws Exception
    {
        Node node = Node.newNode().
            build();

        try
        {
            NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );
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
        Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        assertNotNull( indexDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        assertNotNull( indexDocuments );
        assertNotNull( getIndexDocumentOfType( indexDocuments, "test" ) );
    }

    @Test
    public void set_analyzer()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        final Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
    }

    @Test
    public void node_index_document_meta_data_values()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        Instant modifiedDateTime = LocalDateTime.of( 2013, 1, 2, 3, 4, 5 ).toInstant( ZoneOffset.UTC );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            createdTime( Instant.now() ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TEST_REPOSITORY.getId() ), indexDocument.getIndexName() );
        assertEquals( "test", indexDocument.getIndexTypeName() );

        final AbstractIndexDocumentItem createdTimeItem = getItemWithName( indexDocument, CREATED_TIME_PROPERTY, IndexValueType.DATETIME );

        assertEquals( Date.from( node.getCreatedTime() ), createdTimeItem.getValue() );

        final AbstractIndexDocumentItem creator = getItemWithName( indexDocument, CREATOR_PROPERTY_PATH, IndexValueType.STRING );

        assertEquals( "test:creator", creator.getValue() );

        final AbstractIndexDocumentItem modifier = getItemWithName( indexDocument, MODIFIER_PROPERTY_PATH, IndexValueType.STRING );

        assertEquals( "test:modifier", modifier.getValue() );
    }

    @Test
    public void add_properties_then_index_document_items_created_for_each_property()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( DataPath.from( "a.b.c" ), Value.newDouble( 2.0 ) );
        rootDataSet.setProperty( DataPath.from( "a.b.d" ), Value.newLocalDate( LocalDate.now() ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_d" ), IndexValueType.DATETIME ) );
    }

    @Test
    public void create_for_properties_with_multiple_values()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( DataPath.from( "a.b.c" ), Value.newDouble( 2.0 ) );
        rootDataSet.addProperty( DataPath.from( "a.b.c" ), Value.newDouble( 3.0 ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, TEST_WORKSPACE, TEST_REPOSITORY.getId() );

        assertTrue( indexDocuments.iterator().hasNext() );
        final IndexDocument next = indexDocuments.iterator().next();

        final Set<AbstractIndexDocumentItem> numberItems =
            getItemsWithName( next, IndexDocumentItemPath.from( "a_b_c" ), IndexValueType.NUMBER );

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

    AbstractIndexDocumentItem getItemWithName( final IndexDocument indexDocument, final IndexDocumentItemPath path,
                                               final IndexValueType baseType )
    {
        for ( AbstractIndexDocumentItem item : indexDocument.getIndexDocumentItems() )
        {
            if ( item.getPath().equals( path.toString() ) && item.getIndexBaseType().equals( baseType ) )
            {
                return item;
            }
        }

        return null;
    }

    Set<AbstractIndexDocumentItem> getItemsWithName( final IndexDocument indexDocument, final IndexDocumentItemPath path,
                                                     final IndexValueType baseType )
    {
        Set<AbstractIndexDocumentItem> items = Sets.newHashSet();

        for ( AbstractIndexDocumentItem item : indexDocument.getIndexDocumentItems() )
        {
            if ( item.getPath().equals( path.toString() ) && item.getIndexBaseType().equals( baseType ) )
            {
                items.add( item );
            }
        }

        return items;
    }


}
