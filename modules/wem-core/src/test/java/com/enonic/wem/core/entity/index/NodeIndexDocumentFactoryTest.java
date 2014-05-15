package com.enonic.wem.core.entity.index;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.IndexValueType;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentItemPath;

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
            NodeIndexDocumentFactory.create( node );
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

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        assertNotNull( indexDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        assertNotNull( indexDocuments );
        assertNotNull( getIndexDocumentOfType( indexDocuments, IndexType.NODE ) );
    }

    @Test
    public void set_analyzer()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        final Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            entityIndexConfig( EntityPropertyIndexConfig.newEntityIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
    }

    @Test
    public void node_index_document_meta_data_values()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        Instant modifiedDateTime = new DateTime( 2013, 1, 2, 3, 4, 5 ).toInstant();

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            createdTime( Instant.now() ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            entityIndexConfig( EntityPropertyIndexConfig.newEntityIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( Index.NODB, indexDocument.getIndex() );
        assertEquals( IndexType.NODE, indexDocument.getIndexType() );

        final AbstractIndexDocumentItem createdTimeItem =
            getItemWithName( indexDocument, NodeIndexDocumentFactory.CREATED_TIME_PROPERTY, IndexValueType.INSTANT );

        assertEquals( node.getCreatedTime().toDate(), createdTimeItem.getValue() );

        final AbstractIndexDocumentItem creator =
            getItemWithName( indexDocument, NodeIndexDocumentFactory.CREATOR_PROPERTY_PATH, IndexValueType.STRING );

        assertEquals( "test:creator", creator.getValue() );

        final AbstractIndexDocumentItem modifier =
            getItemWithName( indexDocument, NodeIndexDocumentFactory.MODIFIER_PROPERTY_PATH, IndexValueType.STRING );

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

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_d" ), IndexValueType.INSTANT ) );
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

        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );

        assertTrue( indexDocuments.iterator().hasNext() );
        final IndexDocument next = indexDocuments.iterator().next();

        final Set<AbstractIndexDocumentItem> numberItems =
            getItemsWithName( next, IndexDocumentItemPath.from( "a_b_c" ), IndexValueType.NUMBER );

        assertEquals( 2, numberItems.size() );

    }

    private IndexDocument getIndexDocumentOfType( final Collection<IndexDocument> indexDocuments, final IndexType indexType )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            if ( indexType.equals( indexDocument.getIndexType() ) )
            {
                return indexDocument;
            }
        }
        return null;
    }

    public AbstractIndexDocumentItem getItemWithName( final IndexDocument indexDocument, final IndexDocumentItemPath path,
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

    public Set<AbstractIndexDocumentItem> getItemsWithName( final IndexDocument indexDocument, final IndexDocumentItemPath path,
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
