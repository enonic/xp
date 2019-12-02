package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;

import static com.enonic.xp.repo.impl.TestContext.TEST_BRANCH;
import static com.enonic.xp.repo.impl.TestContext.TEST_REPOSITORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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

        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
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
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
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
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
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
            branch( TEST_BRANCH ).
            repositoryId( TEST_REPOSITORY.getId() ).
            build().
            create();

        final IndexDocument indexDocument = getIndexDocumentOfType( indexDocuments, "test" );

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TEST_REPOSITORY.getId(), TEST_BRANCH ), indexDocument.getIndexName() );
        assertEquals( "test", indexDocument.getIndexTypeName() );
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
}
