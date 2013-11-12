package com.enonic.wem.core.entity.index;

import java.util.Collection;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument2;
import com.enonic.wem.core.index.document.IndexDocumentItemPath;
import com.enonic.wem.core.index.document.IndexValueType;

import static org.junit.Assert.*;

public class NodeIndexDocumentFactoryTest
{
    private NodeIndexDocumentFactory factory = new NodeIndexDocumentFactory();

    @Test
    public void validate_given_no_id_then_exception()
        throws Exception
    {
        Node node = Node.newNode().
            build();

        try
        {
            factory.create( node );
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

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

        assertNotNull( indexDocuments );
    }

    @Test
    public void index_node_document_created()
        throws Exception
    {
        Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            build();

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

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
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

        final IndexDocument2 indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
    }

    @Test
    public void node_index_document_meta_data_values()
        throws Exception
    {
        final String myAnalyzerName = "myAnalyzer";

        DateTime modifiedDateTime = new DateTime( 2013, 01, 02, 03, 04, 05 );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            name( "myName" ).
            createdTime( DateTime.now() ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

        final IndexDocument2 indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( Index.NODB, indexDocument.getIndex() );
        assertEquals( IndexType.NODE, indexDocument.getIndexType() );

        final AbstractIndexDocumentItem createdTimeItem =
            getItemWithName( indexDocument, NodeIndexDocumentFactory.CREATED_TIME_PROPERTY, IndexValueType.DATETIME );

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
        rootDataSet.addProperty( DataPath.from( "a.b.c" ), new Value.Double( 2.0 ) );
        rootDataSet.setProperty( DataPath.from( "a.b.d" ), new Value.DateMidnight( DateMidnight.now() ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            build();

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

        final IndexDocument2 indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_c" ), IndexValueType.NUMBER ) );
        assertNotNull( getItemWithName( indexDocument, IndexDocumentItemPath.from( "a_b_d" ), IndexValueType.DATETIME ) );
    }

    private IndexDocument2 getIndexDocumentOfType( final Collection<IndexDocument2> indexDocuments, final IndexType indexType )
    {
        for ( IndexDocument2 indexDocument : indexDocuments )
        {
            if ( indexType.equals( indexDocument.getIndexType() ) )
            {
                return indexDocument;
            }
        }
        return null;
    }

    public AbstractIndexDocumentItem getItemWithName( final IndexDocument2 indexDocument, final IndexDocumentItemPath path,
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

}
