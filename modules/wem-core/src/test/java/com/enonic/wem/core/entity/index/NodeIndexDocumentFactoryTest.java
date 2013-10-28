package com.enonic.wem.core.entity.index;

import java.util.Collection;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument2;
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
        assertEquals( IndexConstants.NODB_INDEX, indexDocument.getIndex() );
        assertEquals( IndexType.NODE, indexDocument.getIndexType() );

        final AbstractIndexDocumentItem createdTimeItem =
            indexDocument.getItemWithName( NodeIndexDocumentFactory.CREATED_TIME_PROPERTY_NAME, IndexValueType.DATETIME );

        assertEquals( node.getCreatedTime(), createdTimeItem.getValue() );

        final AbstractIndexDocumentItem creator =
            indexDocument.getItemWithName( NodeIndexDocumentFactory.CREATOR_PROPERTY_NAME, IndexValueType.STRING );

        assertEquals( "test:creator", creator.getValue() );

        final AbstractIndexDocumentItem modifier =
            indexDocument.getItemWithName( NodeIndexDocumentFactory.MODIFIER_PROPERTY_NAME, IndexValueType.STRING );

        assertEquals( "test:modifier", modifier.getValue() );

    }

    @Test
    public void add_properties_then_index_document_items_created_for_each_property()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

        final String myAnalyzerName = "myAnalyzer";

        DateTime modifiedDateTime = new DateTime( 2013, 01, 02, 03, 04, 05 );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            rootDataSet( rootDataSet ).
            build();

        final Collection<IndexDocument2> indexDocuments = factory.create( node );

        final IndexDocument2 indexDocument = getIndexDocumentOfType( indexDocuments, IndexType.NODE );

        assertNotNull( indexDocument.getItemWithName( "a", IndexValueType.STRING ) );
        assertNotNull( indexDocument.getItemWithName( "a/b", IndexValueType.STRING ) );
        assertNotNull( indexDocument.getItemWithName( "a/b/c", IndexValueType.STRING ) );
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
}
