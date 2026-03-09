package com.enonic.xp.repo.impl.elasticsearch;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeIndexDocumentFactoryTest
{

    @Test
    void validate_given_no_id_then_exception()
    {
        assertThrows( NullPointerException.class, () -> NodeStoreDocumentFactory.createBuilder()
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( NodePath.ROOT )
            .versionId( NodeVersionId.from( "versionId" ) )
            .timestamp( Instant.now() )
            .build()
            .create() );
    }

    @Test
    void validate_given_id_then_ok()
    {
        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .nodeId( NodeId.from( "abc" ) )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( NodePath.ROOT )
            .versionId( NodeVersionId.from( "versionId" ) )
            .timestamp( Instant.now() )
            .build()
            .create();

        assertNotNull( indexDocument );
    }

    @Test
    void index_node_document_created()
    {
        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .nodeId( NodeId.from( "abc" ) )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( NodePath.ROOT )
            .versionId( NodeVersionId.from( "versionId" ) )
            .timestamp( Instant.now() )
            .build()
            .create();

        assertNotNull( indexDocument );
    }

    @Test
    void set_analyzer()
    {
        final String myAnalyzerName = "myAnalyzer";

        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .nodeId( NodeId.from( "abc" ) )
            .indexConfigDocument(
                PatternIndexConfigDocument.create().analyzer( myAnalyzerName ).defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( NodePath.ROOT )
            .versionId( NodeVersionId.from( "versionId" ) )
            .timestamp( Instant.now() )
            .build()
            .create();

        assertEquals( myAnalyzerName, indexDocument.analyzer() );
    }

    @Test
    void node_index_document_meta_data_values()
    {
        final String myAnalyzerName = "myAnalyzer";

        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .nodeId( NodeId.from( "myId" ) )
            .indexConfigDocument(
                PatternIndexConfigDocument.create().analyzer( myAnalyzerName ).defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( new NodePath( "/my-name" ) )
            .versionId( NodeVersionId.from( "versionId" ) )
            .timestamp( Instant.now() )
            .build()
            .create();

        assertEquals( myAnalyzerName, indexDocument.analyzer() );
    }
}
