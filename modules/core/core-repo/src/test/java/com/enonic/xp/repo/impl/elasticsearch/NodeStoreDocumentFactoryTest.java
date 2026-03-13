package com.enonic.xp.repo.impl.elasticsearch;

import java.time.Instant;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;

class NodeStoreDocumentFactoryTest
{
    @Test
    void references()
    {
        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", new Reference( NodeId.from( "othernode" ) ) );

        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .nodeId( NodeId.from( "mynodeid" ) )
            .data( data )
            .indexConfigDocument( PatternIndexConfigDocument.create().defaultConfig( IndexConfig.MINIMAL ).build() )
            .manualOrderValue( 0L )
            .nodePath( new NodePath( "/myNode" ) )
            .versionId( NodeVersionId.from( "versionid" ) )
            .timestamp( Instant.now() )
            .build()
            .create();

        final IndexItems indexItems = indexDocument.data();
        final Collection<Object> referenceValues = indexItems.asValuesMap().get( NodeIndexPath.REFERENCE.getPath() );
        assertThat( referenceValues ).containsExactly( "othernode" );
    }
}
