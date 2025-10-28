package com.enonic.xp.repo.impl.elasticsearch;

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
import static com.enonic.xp.repo.impl.TestContext.TEST_REPOSITORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeIndexDocumentFactoryTest
{

    @Test
    void validate_given_no_id_then_exception()
    {
        Node node = Node.create().
            build();

        assertThrows( NullPointerException.class, () -> NodeStoreDocumentFactory.createBuilder()
            .node( node )
            .branch( TEST_BRANCH )
            .repositoryId( TEST_REPOSITORY_ID )
            .build()
            .create() );
    }

    @Test
    void validate_given_id_then_ok()
    {
        Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            build();

        final IndexDocument indexDocument =
            NodeStoreDocumentFactory.createBuilder().node( node ).branch( TEST_BRANCH ).repositoryId( TEST_REPOSITORY_ID ).build().create();

        assertNotNull( indexDocument );
    }

    @Test
    void index_node_document_created()
    {
        Node node = Node.create().id( NodeId.from( "abc" ) ).build();

        final IndexDocument indexDocument =
            NodeStoreDocumentFactory.createBuilder().node( node ).branch( TEST_BRANCH ).repositoryId( TEST_REPOSITORY_ID ).build().create();

        assertNotNull( indexDocument );
        assertEquals( "test", indexDocument.getIndexTypeName() );
    }

    @Test
    void set_analyzer()
    {
        final String myAnalyzerName = "myAnalyzer";

        final Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( myAnalyzerName ).
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();

        final IndexDocument indexDocument =
            NodeStoreDocumentFactory.createBuilder().node( node ).branch( TEST_BRANCH ).repositoryId( TEST_REPOSITORY_ID ).build().create();

        assertEquals( "test", indexDocument.getIndexTypeName() );
        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
    }

    @Test
    void node_index_document_meta_data_values()
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

        final IndexDocument indexDocument =
            NodeStoreDocumentFactory.createBuilder().node( node ).branch( TEST_BRANCH ).repositoryId( TEST_REPOSITORY_ID ).build().create();

        assertEquals( myAnalyzerName, indexDocument.getAnalyzer() );
        assertEquals( IndexNameResolver.resolveSearchIndexName( TEST_REPOSITORY_ID ), indexDocument.getIndexName() );
        assertEquals( "test", indexDocument.getIndexTypeName() );
    }
}
